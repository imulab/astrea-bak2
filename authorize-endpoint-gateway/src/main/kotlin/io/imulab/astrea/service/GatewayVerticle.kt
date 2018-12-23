package io.imulab.astrea.service

import com.typesafe.config.Config
import io.imulab.astrea.sdk.oauth.assertType
import io.imulab.astrea.sdk.oauth.error.AccessDenied
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.imulab.astrea.sdk.oauth.request.OAuthRequestProducer
import io.imulab.astrea.sdk.oauth.reserved.Param
import io.imulab.astrea.sdk.oauth.validation.RedirectUriValidator
import io.imulab.astrea.sdk.oidc.request.OidcAuthorizeRequest
import io.imulab.astrea.sdk.oidc.request.OidcRequestForm
import io.imulab.astrea.sdk.oidc.reserved.OidcParam
import io.imulab.astrea.sdk.oidc.reserved.ResponseMode
import io.imulab.astrea.sdk.oidc.validation.*
import io.imulab.astrea.service.authn.AuthenticationHandler
import io.imulab.astrea.service.authz.AuthorizationHandler
import io.imulab.astrea.service.lock.ParameterLocker
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.Json
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import okhttp3.HttpUrl
import org.slf4j.LoggerFactory

class GatewayVerticle(
    private val appConfig: Config,
    private val requestProducer: OAuthRequestProducer,
    private val authenticationHandler: AuthenticationHandler,
    private val authorizationHandler: AuthorizationHandler,
    private val parameterLocker: ParameterLocker,
    private val supportValidator: SupportValidator
) : CoroutineVerticle() {

    private val logger = LoggerFactory.getLogger(GatewayVerticle::class.java)

    override suspend fun start() {
        val router = Router.router(vertx)

        router.get("/")
            .suspendedOAuthHandler { rc ->
                /**
                 * Lock incoming parameters. If param_lock is present, check parameter integrity.
                 */
                parameterLocker.hashParameters(rc)
                try {
                    parameterLocker.verifyParameterLock(rc)
                } catch (e: Exception) {
                    logger.error("Error while verifying parameter lock.", e)
                    throw AccessDenied.byServer("parameter lock is potentially tempered.")
                }

                /**
                 * Parse request parameters and produce the request.
                 */
                val form = OidcRequestForm(
                    rc.request().params().entries().groupBy(
                        keySelector = { e -> e.key },
                        valueTransform = { e -> e.value }
                    ).toMutableMap()
                )
                val request = requestProducer.produce(form).assertType<OidcAuthorizeRequest>().also {
                    rc.setOidcAuthorizeRequest(it)
                }

                /**
                 * Perform preliminary validation.
                 *
                 * Special case: validate redirect_uri and response_mode first. If they are valid, set them on context,
                 * so any errors after can be rendered properly
                 */
                RedirectUriValidator.validate(request)
                rc.put(
                    Param.redirectUri,
                    request.redirectUri.defaultOnEmpty(request.client.redirectUris.first())
                )
                ResponseModeValidator.validate(request)
                rc.put(
                    OidcParam.responseMode,
                    request.responseMode.defaultOnEmpty(ResponseMode.query)
                )
                listOf(MaxAgeValidator, PromptValidator, DisplayValidator, supportValidator).forEach { v ->
                    v.validate(request)
                }

                rc.next()
            }
            .suspendedOAuthHandler { rc ->
                /**
                 * Resolve authentication context, and acquire user authorization.
                 */
                authenticationHandler.authenticateOrRedirect(rc)
                authorizationHandler.authorizeOrRedirect(rc)

                rc.next()
            }
            .suspendedOAuthHandler { rc ->
                /**
                 * Dispatch request to various flow services based on their trait.
                 */
                // todo: dispatch
                rc.response().end("ok")
            }

        vertx.createHttpServer(HttpServerOptions().apply {
            port = appConfig.getInt("service.port")
        }).requestHandler(router).listen()
    }

    private fun Route.suspendedOAuthHandler(block: suspend (RoutingContext) -> Unit): Route {
        handler { rc ->
            CoroutineScope(rc.vertx().dispatcher()).async {
                block(rc)
            }.invokeOnCompletion { e ->
                if (e != null)
                    rc.renderError(e)
            }
        }
        return this
    }

    private fun RoutingContext.renderError(t: Throwable) {
        if (t is RedirectionSignal) {
            response().setStatusCode(307).putHeader("Location", t.url).end()
            return
        }

        val e = if (t is OAuthException) t else ServerError.wrapped(t)
        val redirectUri = get<String>(Param.redirectUri) ?: ""
        val responseMode = get<String>(OidcParam.responseMode) ?: ""

        val r = response().apply {
            e.headers.forEach { t, u -> putHeader(t, u) }
        }

        when {
            redirectUri.isEmpty() -> {
                r.statusCode = e.status
                r.end(Json.encode(e.data))
            }
            responseMode == ResponseMode.query -> {
                val url = HttpUrl.parse(redirectUri)!!.newBuilder().apply {
                    e.data.forEach { t, u -> addQueryParameter(t, u) }
                }.build().toString()
                r.statusCode = 302
                r.putHeader("Location", url).end()
            }
            responseMode == ResponseMode.fragment -> {
                val query = HttpUrl.parse(redirectUri)!!.newBuilder().apply {
                    e.data.forEach { t, u -> addQueryParameter(t, u) }
                }.build().query()
                val url = HttpUrl.parse(redirectUri)!!.newBuilder().fragment(query).build().toString()
                r.statusCode = 302
                r.putHeader("Location", url).end()
            }
            else -> throw IllegalStateException("invalid state during error rendering.")
        }
    }

    private fun String.defaultOnEmpty(default: String): String = if (this.isNotEmpty()) this else default
}

private const val requestKey = "OidcAuthorizeRequest"

internal fun RoutingContext.getOidcAuthorizeRequest(): OidcAuthorizeRequest? =
    get(requestKey)

internal fun RoutingContext.setOidcAuthorizeRequest(r: OidcAuthorizeRequest) {
    put(requestKey, r)
}

internal class RedirectionSignal(val url: String) : RuntimeException()