package io.imulab.astrea.service.client

import io.imulab.astrea.sdk.client.Client
import io.imulab.astrea.sdk.oauth.error.InvalidRequest
import io.imulab.astrea.sdk.oauth.reserved.GrantType
import io.imulab.astrea.sdk.oauth.reserved.ResponseType
import io.imulab.astrea.sdk.oidc.reserved.JweContentEncodingAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.JweKeyManagementAlgorithm
import io.imulab.astrea.service.client.handlers.sha256
import io.imulab.astrea.service.client.handlers.webClient
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.kotlin.ext.web.client.sendAwait
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.jose4j.jwk.JsonWebKeySet
import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.*

fun Client.generateId() {
    id = UUID.randomUUID().toString().replace("-", "").toLowerCase()
}

fun Client.nameOrDefault() {
    if (clientName.isEmpty()) {
        check(id.isNotEmpty())
        clientName = "Client $id"
    }
}

fun Client.responseTypeOrDefault() {
    if (responseTypes.isEmpty())
        responseTypes.add(ResponseType.code)
}

fun Client.grantTypeOrDefault() {
    if (grantTypes.isEmpty())
        grantTypes.add(GrantType.authorizationCode)
}

suspend fun Client.jwksByValueOrResolve(vertx: Vertx) {
    if (jwksUri.isNotEmpty() && jwks.isNotEmpty())
        throw InvalidRequest.unmet("Only one of jwks or jwks_uri can be used.")

    if (jwksUri.isNotEmpty()) {
        val result = webClient(vertx).get(jwksUri).sendAwait()
        if (result.statusCode() != 200)
            throw InvalidRequest.unmet("Unable to resolve jwks_uri. URI returned non-200 code.")

        val body = result.bodyAsString()
        val hash = URI(jwksUri).rawFragment
        if (hash.isNotEmpty()) {
            val bodyHash = sha256().digest(body.toByteArray()).toString(StandardCharsets.UTF_8)
            if (hash != bodyHash)
                throw InvalidRequest.unmet("Content from jwks_uri does not match hash.")
        }
        try {
            JsonWebKeySet(body)
        } catch (e: Exception) {
            throw InvalidRequest.unmet("Content from jwks_uri is not valid Json Web Key Set.")
        }

        jwks = body
    }
}

fun Client.encryptionAlgorithmParity() {
    val ensureParity: (JweKeyManagementAlgorithm, JweContentEncodingAlgorithm) -> Unit = { alg, enc ->
        val k = if (alg == JweKeyManagementAlgorithm.None) 0 else 1
        val e = if (enc == JweContentEncodingAlgorithm.None) 0 else 1
        if (k + e == 1)
            throw InvalidRequest.unmet("Encryption algorithm and encoding must be both provided or both none.")
    }

    ensureParity(idTokenEncryptedResponseAlgorithm, idTokenEncryptedResponseEncoding)
    ensureParity(requestObjectEncryptionAlgorithm, requestObjectEncryptionEncoding)
    ensureParity(userInfoEncryptedResponseAlgorithm, userInfoEncryptedResponseEncoding)
}

suspend fun Client.requestResolution(vertx: Vertx) {
    val checkHash: (String, String) -> String? = { raw, hash ->
        when {
            hash.isEmpty() -> raw
            sha256().digest(raw.toByteArray()).toString(StandardCharsets.UTF_8) == hash -> raw
            else -> null
        }
    }

    coroutineScope {
        requestUris.map { requestUri ->
            withContext(vertx.dispatcher()) {
                async {
                    val hash = URI(requestUri).rawFragment
                    if (requests[requestUri]?.let { checkHash(it, hash) } == null) {
                        val result = webClient(vertx).get(jwksUri).sendAwait()
                        if (result.statusCode() != 200)
                            throw InvalidRequest.unmet("Unable to resolve request_uris. URI returned non-200 code.")

                        val body = result.bodyAsString()
                        if (checkHash(body, hash) == null)
                            throw InvalidRequest.unmet("Content from request_uris does not match hash.")

                        requests[requestUri] = body
                    }
                }
            }
        }.awaitAll()
    }
}