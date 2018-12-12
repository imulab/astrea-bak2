package io.imulab.astrea.service.client.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.readValue
import io.imulab.astrea.sdk.client.Client
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
    private val dbMapper: ObjectMapper
) {

    suspend fun createClient(rc: RoutingContext) {
        val client = apiMapper.readValue<Client>(rc.bodyAsString)

        var plainSecret: String = ""
        client.run {
            generateId()
            nameOrDefault()
            responseTypeOrDefault()
            grantTypeOrDefault()
            jwksByValueOrResolve(rc.vertx())
            encryptionAlgorithmParity()
            requestResolution(rc.vertx())
        }

        try {
            mongoClient.insertAwait("client", JsonObject(dbMapper.convertValue<Map<String, Any>>(client)))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        rc.response().applicationJson {
            json {
                obj(
                    "client_id" to client.id,
                    "client_secret" to plainSecret,
                    "registration_client_uri" to "/client/${client.id}",
                    "client_id_issued_at" to LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                    "client_secret_expires_at" to 0
                )
            }
        }
    }
}