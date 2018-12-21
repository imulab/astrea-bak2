package io.imulab.astrea.service

import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.awaitResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal val logger: Logger = LoggerFactory.getLogger("io.imulab.astrea.service.Main")

suspend fun main() {
    val vertx = Vertx.vertx()

    val gateway = GatewayVerticle()

    try {
        val deploymentId = awaitResult<String> { vertx.deployVerticle(gateway, it) }
        logger.info("Authorize endpoint gateway service successfully deployed with id {}", deploymentId)
    } catch (e: Exception) {
        logger.error("Authorize endpoint gateway service encountered error during deployment.", e)
    }
}