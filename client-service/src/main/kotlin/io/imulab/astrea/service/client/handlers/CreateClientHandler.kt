package io.imulab.astrea.service.client.handlers

import com.fasterxml.jackson.module.kotlin.convertValue
import io.imulab.astrea.sdk.client.Client
import io.imulab.astrea.service.client.*
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.ext.mongo.insertAwait
import java.time.LocalDateTime
import java.time.ZoneOffset

class CreateClientHandler(vertx: Vertx) {

    private val mongoClient: MongoClient = MongoClient.createNonShared(vertx, json {
        obj(
            "host" to "localhost",
            "port" to 32768
        )
    })

    suspend fun createClient(rc: RoutingContext) {
        val client = Json.decodeValue(rc.bodyAsString, Client::class.java)

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

        mongoClient.insertAwait("client", JsonObject.mapFrom(client))

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