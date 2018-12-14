package io.imulab.astrea.service.client

import io.imulab.astrea.service.client.verticle.ClientApiVerticle
import io.imulab.astrea.service.client.verticle.ClientGrpcVerticle
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.kotlin.coroutines.awaitResult
import org.kodein.di.generic.instance
import org.slf4j.LoggerFactory

val rootLogger = LoggerFactory.getLogger("io.imulab.astrea.service.client.Main")

suspend fun main() {

    val vertx = Vertx.vertx(VertxOptions().apply {
        preferNativeTransport = true
    })
    val components = wireComponents(vertx)

    try {
        val clientRest: ClientApiVerticle by components.instance()
        val clientGrpc: ClientGrpcVerticle by components.instance()

        awaitResult<String> { vertx.deployVerticle(clientRest, it) }
            .let { rootLogger.info("Rest API deployed with id {}", it) }

        awaitResult<String> { vertx.deployVerticle(clientGrpc, it) }
            .let { rootLogger.info("GRPC API deployed with id {}", it) }

    } catch (e: Throwable) {
        rootLogger.error("Server encountered error.", e)
    }
}
