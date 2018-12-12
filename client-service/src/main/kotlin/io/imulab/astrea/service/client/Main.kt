package io.imulab.astrea.service.client

import io.imulab.astrea.service.client.verticle.ClientApiVerticle
import io.vertx.core.Vertx
import io.vertx.kotlin.core.deployVerticleAwait


suspend fun main() {
    val vertx = Vertx.vertx()

    try {
        vertx.deployVerticleAwait(ClientApiVerticle::class.java.name)
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}