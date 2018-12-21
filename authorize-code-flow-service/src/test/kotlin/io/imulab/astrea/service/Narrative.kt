package io.imulab.astrea.service

import io.imulab.astrea.sdk.flow.CodeRequest
import io.imulab.astrea.sdk.flow.TokenRequest
import io.imulab.astrea.sdk.flow.asCodeRequestClient
import io.imulab.astrea.sdk.flow.asTokenRequestClient
import io.imulab.astrea.sdk.oauth.reserved.GrantType
import io.imulab.astrea.sdk.oauth.reserved.ResponseType
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.client.NotImplementedOidcClient
import io.imulab.astrea.sdk.oidc.reserved.JweContentEncodingAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.JweKeyManagementAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.StandardScope
import io.imulab.astrea.sdk.oidc.reserved.SubjectType
import org.jose4j.jwk.JsonWebKeySet
import org.jose4j.jwk.RsaJwkGenerator
import org.jose4j.jwk.Use
import java.time.LocalDateTime
import java.time.ZoneOffset

object Narrative {

    val client = object : NotImplementedOidcClient() {
        override val jwks: String = JsonWebKeySet().apply {
            addJsonWebKey(RsaJwkGenerator.generateJwk(2048).also { k ->
                k.keyId = "85479d5e-e687-4d4a-a794-fc50c466da83"
                k.use = Use.SIGNATURE
                k.algorithm = JwtSigningAlgorithm.RS256.algorithmIdentifier
            })
        }.toJson()
        override val jwksUri: String = ""
        override val sectorIdentifierUri: String = "https://test.com/apps.json"
        override val subjectType: String = SubjectType.pairwise
        override val idTokenSignedResponseAlgorithm: JwtSigningAlgorithm = JwtSigningAlgorithm.RS256
        override val idTokenEncryptedResponseAlgorithm: JweKeyManagementAlgorithm = JweKeyManagementAlgorithm.None
        override val idTokenEncryptedResponseEncoding: JweContentEncodingAlgorithm = JweContentEncodingAlgorithm.None
        override val id: String = "1c3cc241-8c33-4fac-8e18-71fd94c341c0"
        override val redirectUris: Set<String> = setOf("https://test.com/callback")
        override val responseTypes: Set<String> = setOf(ResponseType.code, ResponseType.token)
        override val grantTypes: Set<String> = setOf(GrantType.authorizationCode)
        override val scopes: Set<String> = setOf("foo", "bar", "openid", "offline_access")
    }

    val authorizeRequest: CodeRequest = CodeRequest.newBuilder().apply {
        id = "472ffb2b-f7cd-4f14-b99f-2bb48f98b121"
        requestTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        addAllScopes(listOf("foo", StandardScope.openid))
        addResponseTypes(ResponseType.code)
        redirectUri = "https://test.com/callback"
        state = "12345678"
        client = Narrative.client.asCodeRequestClient()
        session = CodeRequest.Session.newBuilder()
            .setSubject("foo@bar.com")
            .addAllGrantedScopes(listOf("foo", StandardScope.openid))
            .setAuthenticationTime(LocalDateTime.now().minusMinutes(1).toEpochSecond(ZoneOffset.UTC))
            .setNonce("87654321")
            .build()
    }.build()

    val tokenRequest: TokenRequest = TokenRequest.newBuilder().apply {
        id = "8e216b61-2986-4f0d-a7f7-b2f10f9e51d9"
        requestTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        grantType = GrantType.authorizationCode
        redirectUri = "https://test.com/callback"
        code = "replace_me"
        client = Narrative.client.asTokenRequestClient()
    }.build()
}