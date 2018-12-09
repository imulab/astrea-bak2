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
            Client(
                id = response.id,
                name = response.name,
                type = response.type,
                redirectUris = response.redirectUrisList.toSet(),
                responseTypes = response.responseTypesList.toSet(),
                grantTypes = response.grantTypesList.toSet(),
                scopes = response.scopesList.toSet(),
                applicationType = response.applicationType,
                contacts = LinkedHashSet(response.contactsList),
                logoUri = response.logoUri,
                clientUri = response.clientUri,
                policyUri = response.policyUri,
                tosUri = response.tosUri,
                jwksUri = "",
                jwks = response.jwks,
                sectorIdentifierUri = response.sectorIdentifierUri,
                subjectType = response.subjectType,
                idTokenSignedResponseAlgorithm = JwtSigningAlgorithm.valueOf(response.idTokenSignedResponseAlgorithm),
                idTokenEncryptedResponseAlgorithm = JweKeyManagementAlgorithm.valueOf(response.idTokenEncryptedResponseAlgorithm),
                idTokenEncryptedResponseEncoding = JweContentEncodingAlgorithm.valueOf(response.idTokenEncryptedResponseEncoding),
                requestObjectSigningAlgorithm = JwtSigningAlgorithm.valueOf(response.requestObjectSigningAlgorithm),
                requestObjectEncryptionAlgorithm = JweKeyManagementAlgorithm.valueOf(response.requestObjectEncryptionAlgorithm),
                requestObjectEncryptionEncoding = JweContentEncodingAlgorithm.valueOf(response.requestObjectEncryptionEncoding),
                userInfoSignedResponseAlgorithm = JwtSigningAlgorithm.valueOf(response.userInfoSignedResponseAlgorithm),
                userInfoEncryptedResponseAlgorithm = JweKeyManagementAlgorithm.valueOf(response.userInfoEncryptedResponseAlgorithm),
                userInfoEncryptedResponseEncoding = JweContentEncodingAlgorithm.valueOf(response.userInfoEncryptedResponseEncoding),
                tokenEndpointAuthenticationMethod = response.tokenEndpointAuthenticationMethod,
                defaultMaxAge = response.defaultMaxAge,
                requireAuthTime = response.requireAuthTime,
                defaultAcrValues = response.defaultAcrValuesList,
                initiateLoginUri = response.initiateLoginUri,
                requestUris = response.requestUrisList
            )
        } catch (e: Exception) {
            throw ServerError.internal(e.localizedMessage)
        }
    }
}