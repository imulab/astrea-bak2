package io.imulab.astrea.service.authorize

import io.imulab.astrea.service.authorize.verticle.AuthorizeApiVerticle
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.kotlin.coroutines.awaitResult
import org.kodein.di.generic.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val rootLogger: Logger = LoggerFactory.getLogger("io.imulab.astrea.service.authorize.Main")

suspend fun main() {
    val vertx = Vertx.vertx(VertxOptions().apply {
        preferNativeTransport = true
    })

    val components = components(vertx)

    try {
        val apiVerticle by components.instance<AuthorizeApiVerticle>()

        awaitResult<String> { vertx.deployVerticle(apiVerticle, it) }
            .let { rootLogger.info("API verticle deployed with id {}", it) }
    } catch (e: Exception) {
        rootLogger.error("Service encountered error.", e)
    }
}