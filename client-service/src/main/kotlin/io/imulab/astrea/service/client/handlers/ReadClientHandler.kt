package io.imulab.astrea.service.client.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.service.client.support.ClientDbJsonSupport
import io.imulab.astrea.service.client.support.applicationJson
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.awaitResult

class ReadClientHandler(
    private val mongoClient: MongoClient,
    private val apiMapper: ObjectMapper
) {
    suspend fun readClient(rc: RoutingContext) {
        val clientId = rc.pathParam("clientId")
        val dbObj = awaitResult<JsonObject?>{
            mongoClient.findOne("client", JsonObject().apply {
                put("_id", clientId)
            }, null, it)
        } ?: throw notFound(clientId)
        val client = ClientDbJsonSupport.fromJsonObject(dbObj)

        rc.response()
            .putHeader("Cache-Control", "no-store")
            .putHeader("Pragma", "no-cache")
            .applicationJson(apiMapper, client)
    }
}

val notFound: (String) -> OAuthException = { id ->
    OAuthException(404, "unknown_client", "Client wit id $id is not found.", mapOf(
        "Cache-Control" to "no-store",
        "Pragma" to "no-cache"
    ))
}