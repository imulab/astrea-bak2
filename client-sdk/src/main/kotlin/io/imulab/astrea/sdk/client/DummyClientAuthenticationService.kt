package io.imulab.astrea.sdk.client

import io.grpc.stub.StreamObserver
import io.imulab.astrea.sdk.oauth.error.InvalidClient

/**
 * A dummy authentication service that authenticates based on the configured option [success].
 * When [success] is true, it forwards request to [DummyClientLookupService]. When [success] is false,
 * it raises error.
 */
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