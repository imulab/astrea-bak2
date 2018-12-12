package io.imulab.astrea.service.client.support

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.JweContentEncodingAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.JweKeyManagementAlgorithm
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

object JwtSigningAlgorithmSerializer : JsonSerializer<JwtSigningAlgorithm>() {
    override fun serialize(value: JwtSigningAlgorithm?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        gen?.writeString(value?.spec ?: "")
    }
}

object JwtSigningAlgorithmDeserializer : JsonDeserializer<JwtSigningAlgorithm>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): JwtSigningAlgorithm {
        if (p == null || p.valueAsString.isNullOrEmpty())
            return JwtSigningAlgorithm.RS256
        return JwtSigningAlgorithm.fromSpec(p.valueAsString)
    }
}

object JweKeyManagementAlgorithmSerializer : JsonSerializer<JweKeyManagementAlgorithm>() {
    override fun serialize(value: JweKeyManagementAlgorithm?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        gen?.writeString(value?.spec ?: "")
    }
}

object JweKeyManagementAlgorithmDeserializer : JsonDeserializer<JweKeyManagementAlgorithm>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): JweKeyManagementAlgorithm {
        if (p == null || p.valueAsString.isNullOrEmpty())
            return JweKeyManagementAlgorithm.None
        return JweKeyManagementAlgorithm.fromSpec(p.valueAsString)
    }
}

object JweContentEncodingAlgorithmSerializer : JsonSerializer<JweContentEncodingAlgorithm>() {
    override fun serialize(value: JweContentEncodingAlgorithm?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        gen?.writeString(value?.spec ?: "")
    }
}

object JweContentEncodingAlgorithmDeserializer : JsonDeserializer<JweContentEncodingAlgorithm>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): JweContentEncodingAlgorithm {
        if (p == null || p.valueAsString.isNullOrEmpty())
            return JweContentEncodingAlgorithm.None
        return JweContentEncodingAlgorithm.fromSpec(p.valueAsString)
    }
}

val clientModule = SimpleModule("client").apply {
    addSerializer(JwtSigningAlgorithm::class.java,
        JwtSigningAlgorithmSerializer
    )
    addSerializer(JweKeyManagementAlgorithm::class.java,
        JweKeyManagementAlgorithmSerializer
    )
    addSerializer(JweContentEncodingAlgorithm::class.java,
        JweContentEncodingAlgorithmSerializer
    )

    addDeserializer(JwtSigningAlgorithm::class.java,
        JwtSigningAlgorithmDeserializer
    )
    addDeserializer(JweKeyManagementAlgorithm::class.java,
        JweKeyManagementAlgorithmDeserializer
    )
    addDeserializer(JweContentEncodingAlgorithm::class.java,
        JweContentEncodingAlgorithmDeserializer
    )
}

val sha256: () -> MessageDigest = { MessageDigest.getInstance("SHA-256") }

val webClient: (Vertx) -> WebClient = { vertx ->
    WebClient.create(vertx, WebClientOptions().apply {
        connectTimeout = 5000
    })
}