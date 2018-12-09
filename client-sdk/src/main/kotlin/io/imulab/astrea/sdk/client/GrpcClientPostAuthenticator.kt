package io.imulab.astrea.sdk.client

import io.grpc.Channel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.imulab.astrea.sdk.oauth.client.OAuthClient
import io.imulab.astrea.sdk.oauth.client.authn.ClientAuthenticator
import io.imulab.astrea.sdk.oauth.error.InvalidClient
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.imulab.astrea.sdk.oauth.request.OAuthRequestForm
import io.imulab.astrea.sdk.oauth.reserved.AuthenticationMethod
import java.util.concurrent.TimeUnit

class GrpcClientPostAuthenticator(
    channel: Channel
) : ClientAuthenticator {

    private val stub = ClientAuthenticationGrpc.newBlockingStub(channel)
    private val unauthorizedError = InvalidClient.unauthorized(AuthenticationMethod.clientSecretPost)

    override fun supports(method: String): Boolean = method == AuthenticationMethod.clientSecretPost

    override suspend fun authenticate(form: OAuthRequestForm): OAuthClient {
        if (form.clientId.isEmpty() || form.clientSecret.isEmpty())
            throw InvalidClient.authenticationRequired()

        val request = ClientAuthenticateRequest.newBuilder()
            .setId(form.clientId)
            .setSecret(form.clientSecret)
            .build()

        val response = try {
            stub.withDeadlineAfter(5, TimeUnit.SECONDS).authenticate(request)
        } catch (e: Exception) {
            when (e) {
                is StatusRuntimeException -> {
                    when (e.status) {
                        Status.UNAVAILABLE -> throw RuntimeException("TODO: temporarily unavailable.")
                        else -> throw unauthorizedError
                    }
                }
                else -> throw unauthorizedError
            }
        }

        return try {
            Client.fromClientLookupResponse(response)
        } catch (e: Exception) {
            throw ServerError.internal(e.localizedMessage)
        }
    }
}