package io.imulab.astrea.sdk.client

import io.grpc.stub.StreamObserver
import io.imulab.astrea.sdk.oauth.error.InvalidClient

class DummyClientAuthenticationService(private val success: Boolean = true)
    : ClientAuthenticationGrpc.ClientAuthenticationImplBase() {

    override fun authenticate(
        request: ClientAuthenticateRequest?,
        responseObserver: StreamObserver<ClientLookupResponse>?
    ) {
        if (!success)
            throw InvalidClient.unauthorized(request?.id ?: "")

        return DummyClientLookupService.find(
            ClientLookupRequest.newBuilder().setId(request?.id ?: "").build(),
            responseObserver
        )
    }
}