package io.imulab.astrea.service

import com.typesafe.config.ConfigFactory
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.awaitResult
import org.kodein.di.generic.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal val logger: Logger = LoggerFactory.getLogger("io.imulab.astrea.service.Main")

suspend fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    val config = ConfigFactory.load()

    val components = Components(vertx, config).bootstrap()
    val gateway by components.instance<GatewayVerticle>()

    try {
        val deploymentId = awaitResult<String> { vertx.deployVerticle(gateway, it) }
        logger.info("Token endpoint gateway service successfully deployed with id {}", deploymentId)
    } catch (e: Exception) {
        logger.error("Token endpoint gateway service encountered error during deployment.", e)
    }
}