package io.imulab.astrea.service.client.handlers

import io.imulab.astrea.sdk.client.Client
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext

suspend fun createClient(rc: RoutingContext) {
    val client = Json.decodeValue(rc.bodyAsString, Client::class.java)
    println(client)
    rc.response().end("ok")

}