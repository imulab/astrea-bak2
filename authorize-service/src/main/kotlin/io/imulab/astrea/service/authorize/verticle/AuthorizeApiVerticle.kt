package io.imulab.astrea.service.authorize.verticle

import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory

class AuthorizeApiVerticle(
    private val apiPort: Int
) : CoroutineVerticle() {

    private val logger = LoggerFactory.getLogger(AuthorizeApiVerticle::class.java)

    override suspend fun start() {
        val router = Router.router(vertx).apply {
            get("/oauth/authorize").handler { rc ->
                logger.info("X-ASTREA-LOGIN = {}", rc.request().getHeader("X-ASTREA-LOGIN"))
                logger.info("X-ASTREA-CONSENT = {}", rc.request().getHeader("X-ASTREA-CONSENT"))
                rc.response().end("ok")
            }
        }

        vertx.createHttpServer(
            HttpServerOptions().apply {
                port = apiPort
            }
        ).requestHandler(router).listen()
    }
}