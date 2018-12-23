package io.imulab.astrea.service.dispatch

import io.imulab.astrea.sdk.flow.AuthorizeCodeFlowGrpc
import io.imulab.astrea.sdk.flow.CodeRequest
import io.imulab.astrea.sdk.oauth.assertType
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.exactly
import io.imulab.astrea.sdk.oauth.reserved.ResponseType
import io.imulab.astrea.sdk.oauth.response.AuthorizeEndpointResponse
import io.imulab.astrea.sdk.oidc.request.OidcAuthorizeRequest
import io.imulab.astrea.sdk.oidc.request.OidcSession
import io.imulab.astrea.service.ResponseRenderer
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory
import java.time.ZoneOffset

class AuthorizeCodeFlow{

    class AuthorizeLeg(
        private val stub: AuthorizeCodeFlowGrpc.AuthorizeCodeFlowBlockingStub
    ) : OAuthDispatcher {

        private val logger = LoggerFactory.getLogger(AuthorizeLeg::class.java)

        override fun supports(request: OidcAuthorizeRequest, rc: RoutingContext): Boolean {
            return request.responseTypes.exactly(ResponseType.code)
        }

        override suspend fun handle(request: OidcAuthorizeRequest, rc: RoutingContext) {
            val response = try {
                stub.authorize(request.asCodeRequest())
            } catch (e: Exception) {
                logger.error("Error calling authorize code flow service.", e)
                throw e
            }

            if (response.success) {
                ResponseRenderer.render(
                    AuthorizeEndpointResponse(
                        code = response.data.code,
                        scope = response.data.scopesList.toSet(),
                        state = request.state
                    ),
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

        private fun OidcAuthorizeRequest.asCodeRequest(): CodeRequest {
            return CodeRequest.newBuilder()
                .setId(id)
                .setRequestTime(requestTime.toEpochSecond(ZoneOffset.UTC))
                .addAllResponseTypes(responseTypes)
                .setRedirectUri(redirectUri)
                .setState(state)
                .addAllScopes(scopes)
                .setClient(
                    CodeRequest.Client.newBuilder()
                        .setId(client.id)
                        .addAllRedirectUris(client.redirectUris)
                        .addAllResponseTypes(client.responseTypes)
                        .addAllScopes(client.scopes)
                        .build()
                )
                .setSession(
                    CodeRequest.Session.newBuilder()
                        .setSubject(session.subject)
                        .addAllGrantedScopes(session.grantedScopes)
                        .setAuthenticationTime(session.assertType<OidcSession>().authTime?.toEpochSecond(ZoneOffset.UTC) ?: 0)
                        .addAllAcrValues(session.assertType<OidcSession>().acrValues)
                        .setNonce(nonce)
                        .build()
                )
                .build()
        }
    }
}