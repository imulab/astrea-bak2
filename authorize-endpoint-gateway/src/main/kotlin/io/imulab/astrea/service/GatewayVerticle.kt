package io.imulab.astrea.service

import com.typesafe.config.Config
import io.imulab.astrea.sdk.oauth.assertType
import io.imulab.astrea.sdk.oauth.request.OAuthRequestProducer
import io.imulab.astrea.sdk.oidc.request.OidcAuthorizeRequest
import io.imulab.astrea.sdk.oidc.request.OidcRequestForm
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

class GatewayVerticle(
    private val appConfig: Config,
    private val requestProducer: OAuthRequestProducer
) : CoroutineVerticle() {

    override suspend fun start() {
        val router = Router.router(vertx)

        router.get("/").suspendHandler { rc ->
            val form = OidcRequestForm(
                rc.request().params().entries().groupBy(
                    keySelector = { e -> e.key },
                    valueTransform = { e -> e.value }
                ).toMutableMap()
            )
            val request = requestProducer.produce(form).assertType<OidcAuthorizeRequest>()

            rc.put("OidcAuthorizeRequest", request)
            rc.next()
        }.suspendHandler { rc ->
            val request = rc.get<OidcAuthorizeRequest>("OidcAuthorizeRequest")

            // basic validation

            rc.next()
        }.suspendHandler { rc ->

            // authentication and validate

            rc.next()
        }.suspendHandler { rc ->

            // consent and validate

            rc.next()
        }.suspendHandler { rc ->

            // dispatch service

            rc.response().end("ok")
        }

        vertx.createHttpServer(HttpServerOptions().apply {
            port = appConfig.getInt("service.port")
        }).requestHandler(router).listen()
    }

    private fun Route.suspendHandler(block: suspend (RoutingContext) -> Unit) : Route {
        handler { rc ->
            CoroutineScope(rc.vertx().dispatcher()).async {
                block(rc)
            }.invokeOnCompletion { e ->
                if (e != null)
                    rc.fail(e)
            }
        }
        return this
    }
}