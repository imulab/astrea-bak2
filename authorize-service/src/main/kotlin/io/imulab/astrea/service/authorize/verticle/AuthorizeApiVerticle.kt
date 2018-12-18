package io.imulab.astrea.service.authorize.verticle

import io.imulab.astrea.sdk.oauth.assertType
import io.imulab.astrea.sdk.oauth.error.InvalidScope
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.imulab.astrea.sdk.oauth.handler.AuthorizeRequestHandler
import io.imulab.astrea.sdk.oauth.request.OAuthRequestProducer
import io.imulab.astrea.sdk.oauth.reserved.Param
import io.imulab.astrea.sdk.oauth.reserved.space
import io.imulab.astrea.sdk.oidc.jwk.authTime
import io.imulab.astrea.sdk.oidc.jwk.scopes
import io.imulab.astrea.sdk.oidc.request.OidcAuthorizeRequest
import io.imulab.astrea.sdk.oidc.request.OidcRequestForm
import io.imulab.astrea.sdk.oidc.request.OidcSession
import io.imulab.astrea.sdk.oidc.reserved.OidcParam
import io.imulab.astrea.sdk.oidc.reserved.ResponseMode
import io.imulab.astrea.sdk.oidc.response.OidcAuthorizeEndpointResponse
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
import org.jose4j.jwt.JwtClaims
import org.slf4j.LoggerFactory

class AuthorizeApiVerticle(
    private val apiPort: Int,
    private val requestProducer: OAuthRequestProducer,
    private val handlers: List<AuthorizeRequestHandler>
) : CoroutineVerticle() {

    private val logger = LoggerFactory.getLogger(AuthorizeApiVerticle::class.java)

    override suspend fun start() {
        vertx.createHttpServer(
            HttpServerOptions().apply {
                port = apiPort
            }
        ).requestHandler(
            Router.router(vertx).apply {
                get("/oauth/authorize").suspendHandler(::authorize)
            }
        ).listen()
    }

    private suspend fun authorize(rc: RoutingContext) {
        try {
            doAuthorize(
                rc.request().params().groupBy(
                    keySelector = { e -> e.key },
                    valueTransform = { e -> e.value }
                ).toMutableMap(),
                rc
            )
        } catch (e: Exception) {
            when (e) {
                is OAuthException -> rc.renderException(e)
                else -> rc.renderException(ServerError.wrapped(e))
            }
        }
    }

    private suspend fun doAuthorize(params: MutableMap<String, List<String>>, rc: RoutingContext) {
        logger.info(params.toString())
        logger.info("X-ASTREA-LOGIN = {}", rc.request().getHeader("X-ASTREA-LOGIN"))
        logger.info("X-ASTREA-CONSENT = {}", rc.request().getHeader("X-ASTREA-CONSENT"))

        val request = requestProducer.produce(OidcRequestForm(params)).assertType<OidcAuthorizeRequest>().apply {
            val loginProviderClaims = JwtClaims.parse(rc.request().getHeader("X-ASTREA-LOGIN") ?: "{}")
            val consentProviderClaims = JwtClaims.parse(rc.request().getHeader("X-ASTREA-CONSENT") ?: "{}")

            session.assertType<OidcSession>().let { s ->
                s.subject = loginProviderClaims.subject
                s.obfuscatedSubject = loginProviderClaims.subject        // todo: use obfuscation
                s.nonce = nonce
                s.authTime = loginProviderClaims.authTime()
                s.grantedScopes.addAll(consentProviderClaims.scopes())
            }
        }
        val response = OidcAuthorizeEndpointResponse()

        // save redirect_uri and response_mode in context so any errors can render properly
        rc.put(Param.redirectUri, request.redirectUri)
        rc.put(OidcParam.responseMode, request.responseMode)

        // todo: validation
        val notGranted = request.scopes.minus(request.session.grantedScopes)
        if (notGranted.isNotEmpty())    // this can move to a validator
            throw InvalidScope.notGranted(notGranted.joinToString(space))

        for (h in handlers)
            h.handleAuthorizeRequest(request, response)
        if (!response.handledResponseTypes.containsAll(request.responseTypes))
            throw ServerError.internal("Some response types were not handled.")

        rc.renderAuthorizeResponse(request, response)
    }

    private fun RoutingContext.renderAuthorizeResponse(request: OidcAuthorizeRequest, response: OidcAuthorizeEndpointResponse) {
        val responseMode = if (request.responseMode.isEmpty()) ResponseMode.query else request.responseMode
        val urlBuilder = HttpUrl.parse(request.redirectUri)!!.newBuilder()

        when (responseMode) {
            ResponseMode.query -> urlBuilder.apply {
                response.data.forEach { t, u -> addQueryParameter(t, u) }
            }
            ResponseMode.fragment -> urlBuilder.apply {
                val query = HttpUrl.parse(request.redirectUri)!!.newBuilder().apply {
                    response.data.forEach { t, u -> addQueryParameter(t, u) }
                }.build().query()
                fragment(query)
            }
        }

        response().apply {
            statusCode = 302
            response.headers.forEach { t, u -> putHeader(t, u) }
            putHeader("Location", urlBuilder.build().toString())
        }.end()
    }

    private fun RoutingContext.renderException(exception: OAuthException) {
        val responseMode = get<String>(OidcParam.responseMode).let {
            if (it.isNullOrEmpty()) ResponseMode.query else it
        }
        val redirectUri = get<String>(Param.redirectUri) ?: ""

        val r = response().apply {
            statusCode = exception.status
            exception.headers.forEach { t, u -> putHeader(t, u) }
        }

        when {
            redirectUri.isEmpty() -> r.end(Json.encode(exception.data))
            responseMode == ResponseMode.query -> {
                val url = HttpUrl.parse(redirectUri)!!.newBuilder().apply {
                    exception.data.forEach { t, u -> addQueryParameter(t, u) }
                }.build().toString()
                r.putHeader("Location", url).end()
            }
            responseMode == ResponseMode.fragment -> {
                val query = HttpUrl.parse(redirectUri)!!.newBuilder().apply {
                    exception.data.forEach { t, u -> addQueryParameter(t, u) }
                }.build().query()
                val url = HttpUrl.parse(redirectUri)!!.newBuilder().fragment(query).build().toString()
                r.putHeader("Location", url).end()
            }
        }
    }

    private fun Route.suspendHandler(block: suspend (RoutingContext) -> Unit) {
        handler { rc ->
            val deferred = CoroutineScope(rc.vertx().dispatcher()).async {
                block(rc)
            }
            deferred.invokeOnCompletion { e ->
                if (e != null)
                    rc.fail(e)
            }
        }
    }
}