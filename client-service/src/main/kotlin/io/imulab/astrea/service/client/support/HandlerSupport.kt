package io.imulab.astrea.service.client.support

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import java.security.MessageDigest

fun HttpServerResponse.applicationJson(mapper: ObjectMapper = Json.prettyMapper, json: Any) {
    putHeader("Content-Type", "application/json")
    end(mapper.writeValueAsString(json))
}

fun HttpServerResponse.applicationJson(mapper: ObjectMapper = Json.prettyMapper, block: () -> Any) {
    putHeader("Content-Type", "application/json")
    end(mapper.writeValueAsString(block()))
}

fun OpenAPI3RouterFactory.addSuspendHandlerByOperationId(operationId: String, block: suspend (RoutingContext) -> Unit): OpenAPI3RouterFactory {
    addHandlerByOperationId(operationId) { rc ->
        val deferred = CoroutineScope(rc.vertx().dispatcher()).async {
            block(rc)
        }
        deferred.invokeOnCompletion { e ->
            if (e != null)
                rc.fail(e)
        }
    }
    return this
}

val sha256: () -> MessageDigest = { MessageDigest.getInstance("SHA-256") }

val webClient: (Vertx) -> WebClient = { vertx ->
    WebClient.create(vertx, WebClientOptions().apply {
        connectTimeout = 5000
    })
}