package io.imulab.astrea.service.client.grpc

import io.grpc.stub.StreamObserver
import io.imulab.astrea.sdk.client.ClientAuthenticateRequest
import io.imulab.astrea.sdk.client.ClientAuthenticationGrpc
import io.imulab.astrea.sdk.client.ClientLookupResponse
import io.imulab.astrea.sdk.oauth.client.pwd.PasswordEncoder
import io.imulab.astrea.sdk.oauth.error.InvalidClient
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.imulab.astrea.service.client.support.ClientDbJsonSupport
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import org.slf4j.LoggerFactory

class ClientAuthenticationService(
    private val mongoClient: MongoClient,
    private val passwordEncoder: PasswordEncoder
) : ClientAuthenticationGrpc.ClientAuthenticationImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun authenticate(
        request: ClientAuthenticateRequest?,
        responseObserver: StreamObserver<ClientLookupResponse>?
    ) {
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
                    responseObserver?.onError(InvalidClient.unknown())
                }
                else -> {
                    val client = ClientDbJsonSupport.fromJsonObject(ar.result())
                    when {
                        client.clientSecret.isEmpty() -> {
                            logger.debug("Client with id {} has no secret.", request?.id)
                            responseObserver?.onError(
                                InvalidClient.authenticationFailedWithReason("client has not secret.")
                            )
                        }
                        !passwordEncoder.matches(request?.secret ?: "", client.clientSecret) -> {
                            logger.debug("Client with id {} secret mismatch.", request?.id)
                            responseObserver?.onError(
                                InvalidClient.authenticationFailed()
                            )
                        }
                        else -> {
                            logger.debug("Client with id {} passed authentication.", request?.id)
                            responseObserver?.onNext(client.toClientLookupResponse())
                            responseObserver?.onCompleted()
                        }
                    }
                }
            }
        }
    }
}