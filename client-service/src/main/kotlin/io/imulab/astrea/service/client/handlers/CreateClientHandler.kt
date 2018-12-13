package io.imulab.astrea.service.client.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
import io.imulab.astrea.sdk.client.Client
import io.imulab.astrea.sdk.oauth.client.pwd.PasswordEncoder
import io.imulab.astrea.sdk.oauth.reserved.AuthenticationMethod
import io.imulab.astrea.service.client.support.*
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.ext.mongo.insertAwait
import java.time.LocalDateTime
import java.time.ZoneOffset

class CreateClientHandler(
    private val mongoClient: MongoClient,
    private val apiMapper: ObjectMapper,
    private val passwordEncoder: PasswordEncoder
) {

    suspend fun createClient(rc: RoutingContext) {
        val client = apiMapper.readValue<Client>(rc.bodyAsString)

        client.run {
            generateId()
            nameOrDefault()
            responseTypeOrDefault()
            grantTypeOrDefault()
            jwksByValueOrResolve(rc.vertx())
            encryptionAlgorithmParity()
            requestResolution(rc.vertx())
        }
        val plainSecret = client.maybeSecret()

        mongoClient.insertAwait("client", ClientDbJsonSupport.toJsonObject(client))

        rc.response().setStatusCode(201).applicationJson {
            json {
                val fields = mutableListOf(
                    "client_id" to client.id,
                    "registration_client_uri" to "/client/${client.id}",
                    "client_id_issued_at" to client.creationTime.toEpochSecond(ZoneOffset.UTC),
                    "client_secret_expires_at" to 0
                )
                if (plainSecret.isNotEmpty())
                    fields.add("client_secret" to plainSecret)
                obj(*fields.toTypedArray())
            }
        }
    }

    private fun Client.maybeSecret(): String {
        return when (tokenEndpointAuthMethod) {
            AuthenticationMethod.clientSecretBasic,
            AuthenticationMethod.clientSecretPost,
            io.imulab.astrea.sdk.oidc.reserved.AuthenticationMethod.clientSecretJwt -> {
                val plainSecret = PasswordGenerator.generateAlphaNumericPassword(32)
                clientSecret = passwordEncoder.encode(plainSecret)
                plainSecret
            }
            else -> ""
        }
    }
}