package io.imulab.astrea.service.dispatch

import io.imulab.astrea.sdk.flow.implicit.ImplicitFlowGrpc
import io.imulab.astrea.sdk.flow.implicit.ImplicitTokenResponse
import io.imulab.astrea.sdk.flow.implicit.asImplicitTokenRequest
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.exactly
import io.imulab.astrea.sdk.oauth.reserved.ResponseType.token
import io.imulab.astrea.sdk.oidc.request.OidcAuthorizeRequest
import io.imulab.astrea.sdk.oidc.reserved.ResponseType.idToken
import io.imulab.astrea.sdk.oidc.response.OidcAuthorizeEndpointResponse
import io.imulab.astrea.service.ResponseRenderer
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

class ImplicitFlow(private val stub: ImplicitFlowGrpc.ImplicitFlowBlockingStub) : OAuthDispatcher {

    private val logger = LoggerFactory.getLogger(ImplicitFlow::class.java)

    override fun supports(request: OidcAuthorizeRequest, rc: RoutingContext): Boolean {
        return when (request.responseTypes.size) {
            1 -> request.responseTypes.exactly(token) || request.responseTypes.exactly(idToken)
            2 -> request.responseTypes.containsAll(listOf(token, idToken))
            else -> false
        }
    }

    override suspend fun handle(request: OidcAuthorizeRequest, rc: RoutingContext) {
        val response : ImplicitTokenResponse = try {
            stub.authorize(request.asImplicitTokenRequest())
        } catch (e: Exception) {
            logger.error("Error calling implicit flow service.", e)
            throw e
        }

        if (response.success) {
            ResponseRenderer.render(
                OidcAuthorizeEndpointResponse().apply {
                    accessToken = response.data.accessToken
                    tokenType = response.data.tokenType
                    expiresIn = response.data.expiresIn
                    idToken = response.data.idToken
                    scope = response.data.scopesList.toSet()
                    state = request.state
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
}