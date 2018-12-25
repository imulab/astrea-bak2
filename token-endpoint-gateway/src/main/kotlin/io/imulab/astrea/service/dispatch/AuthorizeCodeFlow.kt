package io.imulab.astrea.service.dispatch

import io.imulab.astrea.sdk.flow.AuthorizeCodeFlowGrpc
import io.imulab.astrea.sdk.flow.TokenRequest
import io.imulab.astrea.sdk.oauth.assertType
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.exactly
import io.imulab.astrea.sdk.oauth.request.OAuthAccessRequest
import io.imulab.astrea.sdk.oauth.reserved.GrantType
import io.imulab.astrea.sdk.oauth.reserved.dot
import io.imulab.astrea.sdk.oauth.reserved.space
import io.imulab.astrea.sdk.oidc.client.OidcClient
import io.imulab.astrea.sdk.oidc.response.OidcTokenEndpointResponse
import io.imulab.astrea.service.ResponseRenderer
import io.vertx.ext.web.RoutingContext
import java.time.ZoneOffset

class AuthorizeCodeFlow {

    class TokenLeg(
        private val authorizationCodeFlowPrefix: String,
        private val stub: AuthorizeCodeFlowGrpc.AuthorizeCodeFlowBlockingStub
    ) : OAuthDispatcher {

        override fun supports(request: OAuthAccessRequest, rc: RoutingContext): Boolean {
            return request.grantTypes.exactly(GrantType.authorizationCode) &&
                    request.code.startsWith(authorizationCodeFlowPrefix + dot)
        }

        override suspend fun handle(request: OAuthAccessRequest, rc: RoutingContext) {
            val response = stub.exchange(request.asTokenRequest())

            if (response.success) {
                ResponseRenderer.render(
                    OidcTokenEndpointResponse().apply {
                        accessToken = response.data.accessToken ?: ""
                        tokenType = response.data.tokenType ?: ""
                        expiresIn = response.data.expiresIn
                        idToken = response.data.idToken ?: ""
                    },
                    rc
                )
            } else {
                throw OAuthException(
                    status = response.failure.status,
                    error = response.failure.error,
                    description = response.failure.description,
                    headers = response.failure.headersMap
                )
            }
        }

        private fun OAuthAccessRequest.asTokenRequest(): TokenRequest {
            return TokenRequest.newBuilder()
                .setId(id)
                .setRequestTime(requestTime.toEpochSecond(ZoneOffset.UTC))
                .setCode(code)
                .setGrantType(grantTypes.joinToString(space))
                .setRedirectUri(redirectUri)
                .setClient(
                    client.assertType<OidcClient>().let {
                        TokenRequest.Client.newBuilder()
                            .setId(it.id)
                            .addAllGrantTypes(it.grantTypes)
                            .addAllRedirectUris(it.redirectUris)
                            .setJwks(it.jwks)
                            .setSectorIdentifierUri(it.sectorIdentifierUri)
                            .setSubjectType(it.subjectType)
                            .setIdTokenSignedResponseAlgorithm(it.idTokenSignedResponseAlgorithm.spec)
                            .setIdTokenEncryptedResponseAlgorithm(it.idTokenEncryptedResponseAlgorithm.spec)
                            .setIdTokenEncryptedResponseEncoding(it.idTokenEncryptedResponseEncoding.spec)
                            .build()
                    }
                )
                .build()
        }
    }
}