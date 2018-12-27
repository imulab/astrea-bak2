package io.imulab.astrea.service

import com.typesafe.config.ConfigFactory
import io.vertx.core.Vertx
import org.kodein.di.generic.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal val logger: Logger = LoggerFactory.getLogger("io.imulab.astrea.service.MainKt")

fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    val config = ConfigFactory.load()
    val components = Components(vertx, config).bootstrap()

    val grpcVerticle by components.instance<GrpcVerticle>()
    vertx.deployVerticle(grpcVerticle) { ar ->
        if (ar.succeeded()) {
            logger.info("Client credentials flow service successfully deployed with id {}", ar.result())
        } else {
            logger.error("Client credentials flow service failed to deploy.", ar.cause())
        }
    }
}
