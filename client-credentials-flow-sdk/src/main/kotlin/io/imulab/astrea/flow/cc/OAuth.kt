package io.imulab.astrea.flow.cc

import io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenRequest
import io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenResponse
import io.imulab.astrea.sdk.oauth.assertType
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.request.OAuthAccessRequest
import io.imulab.astrea.sdk.oauth.response.TokenEndpointResponse
import io.imulab.astrea.sdk.oidc.client.NotImplementedOidcClient
import io.imulab.astrea.sdk.oidc.client.OidcClient
import io.imulab.astrea.sdk.oidc.request.OidcSession
import java.time.ZoneOffset

class ClientCredentialsFlowClient(source: ClientCredentialsTokenRequest.Client) : NotImplementedOidcClient() {
    override val id: String = source.id
    override val type: String = source.type
    override val scopes: Set<String> = source.scopesList.toSet()
    override val grantTypes: Set<String> = source.grantTypesList.toSet()
}

fun ClientCredentialsTokenRequest.toAccessRequest(): OAuthAccessRequest {
    return OAuthAccessRequest.Builder().also { b ->
        b.grantTypes.addAll(grantTypesList)
        b.scopes.addAll(scopesList)
        b.client = ClientCredentialsFlowClient(client)
        b.session = OidcSession()
    }.build()
}

fun OAuthAccessRequest.toClientCredentialsTokenRequest(): ClientCredentialsTokenRequest {
    return ClientCredentialsTokenRequest.newBuilder()
        .setId(id)
        .setRequestTime(requestTime.toEpochSecond(ZoneOffset.UTC))
        .addAllGrantTypes(grantTypes)
        .addAllScopes(scopes)
        .setClient(client.assertType<OidcClient>().toTokenRequestClient())
        .build()
}

internal fun OidcClient.toTokenRequestClient(): ClientCredentialsTokenRequest.Client {
    return ClientCredentialsTokenRequest.Client.newBuilder()
        .setId(id)
        .setType(type)
        .addAllGrantTypes(grantTypes)
        .addAllScopes(scopes)
        .build()
}

fun TokenEndpointResponse.toClientCredentialsTokenResponse(): ClientCredentialsTokenResponse {
    return ClientCredentialsTokenResponse.newBuilder()
        .setSuccess(true)
        .setData(
            ClientCredentialsTokenResponse.TokenPackage.newBuilder()
                .setAccessToken(accessToken)
                .setTokenType(tokenType)
                .setExpiresIn(expiresIn)
                .addAllScopes(scope)
                .setRefreshToken(refreshToken)
                .build()
        )
        .build()
}

fun ClientCredentialsTokenResponse.TokenPackage.toTokenEndpointResponse(): TokenEndpointResponse {
    return TokenEndpointResponse(
        accessToken = accessToken ?: "",
        expiresIn = expiresIn,
        tokenType = tokenType ?: "",
        scope = scopesList?.toSet() ?: emptySet(),
        refreshToken = refreshToken ?: ""
    )
}

fun OAuthException.toFailureResponse(): ClientCredentialsTokenResponse {
    return ClientCredentialsTokenResponse.newBuilder()
        .setSuccess(false)
        .setFailure(
            ClientCredentialsTokenResponse.Failure.newBuilder()
                .setStatus(status)
                .setError(error)
                .setDescription(description)
                .putAllHeaders(headers)
                .build()
        )
        .build()
}

fun ClientCredentialsTokenResponse.Failure.toOAuthException(): OAuthException {
    return OAuthException(
        status = status,
        error = error,
        description = description,
        headers = headersMap
    )
}