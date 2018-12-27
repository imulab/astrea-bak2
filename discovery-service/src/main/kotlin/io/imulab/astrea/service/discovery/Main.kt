package io.imulab.astrea.service.discovery

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.imulab.astrea.sdk.discovery.Discovery
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.json.Json
import io.vertx.ext.healthchecks.HealthCheckHandler
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets

private val rootLogger = LoggerFactory.getLogger("io.imulab.astrea.service.discovery.Main")

object Service

fun main() {
    val vertx = Vertx.vertx(VertxOptions().apply {
        preferNativeTransport = true
    })

    listOf(Json.mapper, Json.prettyMapper).forEach {
        it.apply {
            registerKotlinModule()
            registerModule(SimpleModule().apply {
                addMixIn(Discovery::class.java, DiscoveryMixin::class.java)
            })
        }
    }

    val config = ConfigFactory.load()
    val discovery = readDiscovery(vertx, config)
    val healthCheckHandler = HealthCheckHandler.create(vertx)

    vertx.deployVerticle(DiscoveryHttpVerticle(discovery, config, healthCheckHandler)) { ar ->
        if (ar.failed()) {
            rootLogger.error("Server encountered error.", ar.cause())
        } else {
            rootLogger.info("Http API deployed with id {}", ar.result())
        }
    }

    vertx.deployVerticle(DiscoveryGrpcVerticle(discovery, config, healthCheckHandler)) { ar ->
        if (ar.failed()) {
            rootLogger.error("Server encountered error.", ar.cause())
        } else {
            rootLogger.info("Grpc API deployed with id {}", ar.result())
        }
    }
}

private fun readDiscovery(vertx: Vertx, config: Config): Discovery {
    val source = if (config.hasPath("discovery.source")) config.getString("discovery.source") else ""
    if (!source.isNullOrEmpty()) {
        return Json.decodeValue(vertx.fileSystem().readFileBlocking(source), Discovery::class.java)
    }

    val json = Service.javaClass.classLoader.getResourceAsStream(config.getString("discovery.default"))
        .reader(StandardCharsets.UTF_8)
        .readText()
    return Json.decodeValue(json, Discovery::class.java)
}