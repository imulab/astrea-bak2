package io.imulab.astrea.service

import com.typesafe.config.Config
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import org.slf4j.LoggerFactory

class GatewayVerticle(
    private val appConfig: Config
) : CoroutineVerticle() {

    private val logger = LoggerFactory.getLogger(GatewayVerticle::class.java)

    override suspend fun start() {
        val router = Router.router(vertx)

        router.get("/")
            .suspendedOAuthHandler { rc ->
                // validation
                rc.next()
            }
            .suspendedOAuthHandler { rc ->
                // authentication
                rc.next()
            }
            .suspendedOAuthHandler { rc ->
                // dispatch
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
                    ResponseRenderer.render(e, rc)
            }
        }
        return this
    }
}