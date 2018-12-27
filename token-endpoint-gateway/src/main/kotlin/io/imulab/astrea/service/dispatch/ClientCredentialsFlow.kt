package io.imulab.astrea.service.dispatch

import io.imulab.astrea.flow.cc.toClientCredentialsTokenRequest
import io.imulab.astrea.flow.cc.toOAuthException
import io.imulab.astrea.flow.cc.toTokenEndpointResponse
import io.imulab.astrea.sdk.flow.cc.ClientCredentialsFlowGrpc
import io.imulab.astrea.sdk.oauth.exactly
import io.imulab.astrea.sdk.oauth.request.OAuthAccessRequest
import io.imulab.astrea.sdk.oauth.reserved.GrantType
import io.imulab.astrea.service.ResponseRenderer
import io.vertx.ext.web.RoutingContext

class ClientCredentialsFlow(
    private val stub: ClientCredentialsFlowGrpc.ClientCredentialsFlowBlockingStub
) : OAuthDispatcher {

    override fun supports(request: OAuthAccessRequest, rc: RoutingContext): Boolean {
        return request.grantTypes.exactly(GrantType.clientCredentials)
    }

    override suspend fun handle(request: OAuthAccessRequest, rc: RoutingContext) {
        val response = stub.exchange(request.toClientCredentialsTokenRequest())

        if (response.success) {
            ResponseRenderer.render(response.data.toTokenEndpointResponse(), rc)
        } else {
            throw response.failure.toOAuthException()
        }
    }
}