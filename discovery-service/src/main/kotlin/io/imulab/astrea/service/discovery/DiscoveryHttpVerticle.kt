package io.imulab.astrea.service.discovery

import com.typesafe.config.Config
import io.imulab.astrea.sdk.discovery.Discovery
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.Json
import io.vertx.ext.web.Router

class DiscoveryHttpVerticle(discovery: Discovery, private val appConfig: Config) : AbstractVerticle() {

    private val discoveryJson = Json.encodePrettily(discovery)

    override fun start() {
        val router = Router.router(vertx).apply {
            get("/").handler { rc ->
                rc.response().putHeader("Content-Type", "application/json").end(discoveryJson)
            }
        }

        vertx.createHttpServer(HttpServerOptions().apply {
            port = appConfig.getInt("service.restPort")
        }).requestHandler(router).listen()
    }
}
