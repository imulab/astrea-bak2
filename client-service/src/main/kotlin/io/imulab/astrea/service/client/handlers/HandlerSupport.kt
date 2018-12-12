package io.imulab.astrea.service.client.handlers

import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun HttpServerResponse.applicationJson(json: Any) {
    putHeader("Content-Type", "application/json")
    end(Json.encodePrettily(json))
}

fun HttpServerResponse.applicationJson(block: () -> Any) {
    putHeader("Content-Type", "application/json")
    end(Json.encodePrettily(block()))
}

fun OpenAPI3RouterFactory.addSuspendHandlerByOperationId(operationId: String, block: suspend (RoutingContext) -> Unit): OpenAPI3RouterFactory {
    addHandlerByOperationId(operationId) { rc ->
        CoroutineScope(rc.vertx().dispatcher()).launch {
            block(rc)
        }
    }
    return this
}