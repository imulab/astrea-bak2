package io.imulab.astrea.service.client.grpc

import io.grpc.stub.StreamObserver
import io.imulab.astrea.sdk.client.ClientLookupGrpc
import io.imulab.astrea.sdk.client.ClientLookupRequest
import io.imulab.astrea.sdk.client.ClientLookupResponse
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.imulab.astrea.service.client.handlers.notFound
import io.imulab.astrea.service.client.support.ClientDbJsonSupport
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import org.slf4j.LoggerFactory

class ClientLookupService(private val mongoClient: MongoClient) : ClientLookupGrpc.ClientLookupImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun find(request: ClientLookupRequest?, responseObserver: StreamObserver<ClientLookupResponse>?) {
        mongoClient.findOne("client", JsonObject().apply {
            put("_id", request?.id)
        }, null) { ar ->
            when {
                ar.failed() -> {
                    logger.error("Failed to find client with id {}.", request?.id)
                    responseObserver?.onError(ServerError.wrapped(ar.cause()))
                }
                ar.result() == null -> {
                    logger.debug("Did not find client with id {}.", request?.id)
                    responseObserver?.onError(notFound(request?.id ?: ""))
                }
                else -> {
                    logger.debug("Found client with id {}.", request?.id)

                    val client = ClientDbJsonSupport.fromJsonObject(ar.result())
                    responseObserver?.onNext(client.toClientLookupResponse())
                    responseObserver?.onCompleted()
                }
            }
        }
    }
}