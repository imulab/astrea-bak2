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
import io.imulab.astrea.sdk.oauth.reserved.colon
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeUnit

class GrpcClientBasicAuthenticator(
    channel: Channel,
    private val decoder: Base64.Decoder = Base64.getDecoder()
) : ClientAuthenticator {

    private val stub = ClientAuthenticationGrpc.newBlockingStub(channel)
    private val unauthorizedError = InvalidClient.unauthorized(AuthenticationMethod.clientSecretBasic)

    override fun supports(method: String): Boolean = method == AuthenticationMethod.clientSecretBasic

    override suspend fun authenticate(form: OAuthRequestForm): OAuthClient {
        val header = form.authorizationHeader
        when {
            header.isEmpty() ->
                throw unauthorizedError
            !header.startsWith("Basic ") ->
                throw unauthorizedError
        }

        val clientId: String
        val clientSecret: String
        try {
            val parts = String(decoder.decode(header.removePrefix("Basic ")), StandardCharsets.UTF_8)
                .split(colon)
            when {
                parts.size != 2 -> throw unauthorizedError
                parts[0].isEmpty() -> throw unauthorizedError
                parts[1].isEmpty() -> throw unauthorizedError
                else -> {
                    clientId = parts[0]
                    clientSecret = parts[1]
                }
            }
        } catch (_: IllegalArgumentException) {
            throw unauthorizedError
        }

        assert(clientId.isNotEmpty())
        assert(clientSecret.isNotEmpty())

        val request = ClientAuthenticateRequest.newBuilder()
            .setId(clientId)
            .setSecret(clientSecret)
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