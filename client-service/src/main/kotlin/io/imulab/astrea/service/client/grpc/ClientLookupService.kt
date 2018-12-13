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
                    responseObserver?.onNext(
                        ClientLookupResponse
                            .newBuilder()
                            .setId(client.id)
                            .setName(client.name)
                            .setType(client.type)
                            .addAllRedirectUris(client.redirectUris)
                            .addAllResponseTypes(client.responseTypes)
                            .addAllGrantTypes(client.grantTypes)
                            .addAllScopes(client.scopes)
                            .setApplicationType(client.applicationType)
                            .addAllContacts(client.contacts)
                            .setLogoUri(client.logoUri)
                            .setClientUri(client.clientUri)
                            .setPolicyUri(client.policyUri)
                            .setTosUri(client.tosUri)
                            .setJwks(client.jwks)
                            .setSectorIdentifierUri(client.sectorIdentifierUri)
                            .setSubjectType(client.subjectType)
                            .setIdTokenSignedResponseAlgorithm(client.idTokenSignedResponseAlg)
                            .setIdTokenEncryptedResponseAlgorithm(client.idTokenEncryptedResponseAlg)
                            .setIdTokenEncryptedResponseEncoding(client.idTokenEncryptedResponseEnc)
                            .setRequestObjectSigningAlgorithm(client.requestObjectSigningAlg)
                            .setRequestObjectEncryptionAlgorithm(client.requestObjectEncryptionAlg)
                            .setRequestObjectEncryptionEncoding(client.requestObjectEncryptionEnc)
                            .setUserInfoSignedResponseAlgorithm(client.userinfoSignedResponseAlg)
                            .setUserInfoEncryptedResponseAlgorithm(client.userinfoEncryptedResponseAlg)
                            .setUserInfoEncryptedResponseEncoding(client.userinfoEncryptedResponseEnc)
                            .setTokenEndpointAuthenticationMethod(client.tokenEndpointAuthMethod)
                            .setDefaultMaxAge(client.defaultMaxAge)
                            .setRequireAuthTime(client.requireAuthTime)
                            .addAllDefaultAcrValues(client.defaultAcrValues)
                            .setInitiateLoginUri(client.initiateLoginUri)
                            .addAllRequestUris(client.requestUris)
                            .build()
                    )
                    responseObserver?.onCompleted()
                }
            }
        }
    }
}