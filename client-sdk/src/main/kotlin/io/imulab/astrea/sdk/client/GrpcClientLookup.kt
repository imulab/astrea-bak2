package io.imulab.astrea.sdk.client

import io.grpc.Channel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.imulab.astrea.sdk.oauth.client.ClientLookup
import io.imulab.astrea.sdk.oauth.client.OAuthClient
import io.imulab.astrea.sdk.oauth.error.InvalidClient
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.JweContentEncodingAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.JweKeyManagementAlgorithm
import java.util.concurrent.TimeUnit

/**
 * A gRPC implementation to the [ClientLookup]. The implementation calls the gRPC remote client service to get
 * the client. The client returned does not have a [OAuthClient.secret] field.
 */
class GrpcClientLookup(channel: Channel) : ClientLookup {

    private val stub = ClientLookupGrpc.newBlockingStub(channel)

    override suspend fun find(identifier: String): OAuthClient {
        val request = ClientLookupRequest.newBuilder()
            .setId(identifier)
            .build()

        val response = try {
            stub.withDeadlineAfter(5, TimeUnit.SECONDS).find(request)
        } catch (e: Exception) {
            when (e) {
                is StatusRuntimeException -> {
                    when (e.status) {
                        Status.UNAVAILABLE -> throw RuntimeException("TODO: temporarily unavailable.")
                        else -> throw InvalidClient.unknown()
                    }
                }
                else -> throw InvalidClient.unknown()
            }
        }

        return try {
            Client.fromClientLookupResponse(response)
        } catch (e: Exception) {
            throw ServerError.internal(e.localizedMessage)
        }
    }
}