package io.imulab.astrea.sdk.flow.implicit

import io.imulab.astrea.sdk.oauth.assertType
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.client.NotImplementedOidcClient
import io.imulab.astrea.sdk.oidc.client.OidcClient
import io.imulab.astrea.sdk.oidc.request.OidcAuthorizeRequest
import io.imulab.astrea.sdk.oidc.request.OidcSession
import io.imulab.astrea.sdk.oidc.reserved.JweContentEncodingAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.JweKeyManagementAlgorithm
import java.time.LocalDateTime
import java.time.ZoneOffset

fun OidcAuthorizeRequest.asImplicitTokenRequest(): ImplicitTokenRequest {
    return ImplicitTokenRequest.newBuilder()
        .setId(id)
        .setRequestTime(requestTime.toEpochSecond(ZoneOffset.UTC))
        .addAllResponseTypes(responseTypes)
        .setRedirectUri(redirectUri)
        .setState(state)
        .addAllScopes(scopes)
        .setNonce(nonce)
        .setClient(client.assertType<OidcClient>().asTokenRequestClient())
        .setSession(session.assertType<OidcSession>().asTokenRequestSession())
        .build()
}

private fun OidcClient.asTokenRequestClient(): ImplicitTokenRequest.Client {
    return ImplicitTokenRequest.Client.newBuilder()
        .setId(id)
        .addAllResponseTypes(responseTypes)
        .addAllGrantTypes(grantTypes)
        .addAllRedirectUris(redirectUris)
        .addAllScopes(scopes)
        .setJwks(jwks)
        .setIdTokenSignedResponseAlgorithm(idTokenSignedResponseAlgorithm.spec)
        .setIdTokenEncryptedResponseAlgorithm(idTokenEncryptedResponseAlgorithm.spec)
        .setIdTokenEncryptedResponseEncoding(idTokenEncryptedResponseEncoding.spec)
        .build()
}

private fun OidcSession.asTokenRequestSession(): ImplicitTokenRequest.Session {
    return ImplicitTokenRequest.Session.newBuilder()
        .setSubject(subject)
        .setObfuscatedSubject(obfuscatedSubject)
        .addAllGrantedScopes(grantedScopes)
        .addAllAcrValues(acrValues)
        .setAuthenticationTime(authTime?.toEpochSecond(ZoneOffset.UTC) ?: 0)
        .setNonce(nonce)
        .build()
}

class ImplicitFlowClient(source: ImplicitTokenRequest.Client) : NotImplementedOidcClient() {
    override val id: String = source.id
    override val responseTypes: Set<String> = source.responseTypesList.toSet()
    override val redirectUris: Set<String> = source.redirectUrisList.toSet()
    override val scopes: Set<String> = source.scopesList.toSet()
    override val grantTypes: Set<String> = source.grantTypesList.toSet()
    override val idTokenSignedResponseAlgorithm: JwtSigningAlgorithm =
        JwtSigningAlgorithm.fromSpec(source.idTokenSignedResponseAlgorithm)
    override val idTokenEncryptedResponseAlgorithm: JweKeyManagementAlgorithm =
        JweKeyManagementAlgorithm.fromSpec(source.idTokenEncryptedResponseAlgorithm)
    override val idTokenEncryptedResponseEncoding: JweContentEncodingAlgorithm =
        JweContentEncodingAlgorithm.fromSpec(source.idTokenEncryptedResponseEncoding)
    override val jwksUri: String = ""
    override val jwks: String = source.jwks
}

fun ImplicitTokenRequest.toOidcAuthorizeRequest(): OidcAuthorizeRequest {
    return OidcAuthorizeRequest.Builder().also { b ->
        b.responseTypes.addAll(responseTypesList)
        b.redirectUri = redirectUri ?: ""
        b.state = state ?: ""
        b.scopes.addAll(scopesList)
        b.client = ImplicitFlowClient(client)
        b.nonce = nonce
        b.session = OidcSession().also { s ->
            s.subject = session.subject ?: ""
            s.obfuscatedSubject = session.obfuscatedSubject ?: ""
            s.authTime = LocalDateTime.ofEpochSecond(session.authenticationTime, 0, ZoneOffset.UTC)
            s.acrValues.addAll(session.acrValuesList)
            s.nonce = nonce ?: session.nonce ?: ""
            s.grantedScopes.addAll(session.grantedScopesList)
        }
    }.build()
}