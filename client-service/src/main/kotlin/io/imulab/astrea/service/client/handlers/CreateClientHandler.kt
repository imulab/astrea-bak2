package io.imulab.astrea.service.client.handlers

import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.delay

suspend fun createClient(rc: RoutingContext) {
    delay(200)
    rc.response().end("hello world")
}