package io.imulab.astrea.service.discovery

import io.imulab.astrea.sdk.discovery.Discovery
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.Json
import io.vertx.ext.web.Router

class DiscoveryHttpVerticle(discovery: Discovery) : AbstractVerticle() {

    private val discoveryJson = Json.encodePrettily(discovery)

    override fun start() {
        val router = Router.router(vertx).apply {
            get("/").handler { rc ->
                rc.response().putHeader("Content-Type", "application/discoveryJson").end(discoveryJson)
            }
        }

        vertx.createHttpServer(HttpServerOptions().apply {
            host = "localhost"
            port = 8080
        }).requestHandler(router).listen()
    }
}
