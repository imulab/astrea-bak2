package io.imulab.astrea.service

import com.typesafe.config.Config
import io.imulab.astrea.sdk.flow.TokenRequest
import io.imulab.astrea.sdk.oauth.request.OAuthRequest
import io.imulab.astrea.sdk.oauth.reserved.AuthenticationMethod
import io.imulab.astrea.sdk.oauth.response.AccessTokenResponse
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oauth.token.storage.AccessTokenRepository
import io.imulab.astrea.sdk.oidc.client.NotImplementedOidcClient
import io.imulab.astrea.sdk.oidc.discovery.Discovery
import io.imulab.astrea.sdk.oidc.discovery.OidcContext
import io.imulab.astrea.sdk.oidc.jwk.JsonWebKeySetRepository
import io.imulab.astrea.sdk.oidc.jwk.JsonWebKeySetStrategy
import io.imulab.astrea.sdk.oidc.request.OidcAuthorizeRequest
import io.imulab.astrea.sdk.oidc.request.OidcSession
import io.imulab.astrea.sdk.oidc.reserved.JweContentEncodingAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.JweKeyManagementAlgorithm
import io.imulab.astrea.sdk.oidc.spi.HttpResponse
import io.imulab.astrea.sdk.oidc.spi.SimpleHttpClient
import org.jose4j.jwk.JsonWebKeySet
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

internal val shouldNotBeCalled: () -> Nothing = { throw NotImplementedError("this should not be called.") }

class ImplicitFlowClient(source: TokenRequest.Client) : NotImplementedOidcClient() {
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

fun TokenRequest.toOidcAuthorizeRequest(): OidcAuthorizeRequest {
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

class ServiceContext(
    private val discovery: Discovery,
    config: Config
) : OidcContext, Discovery by discovery {
    override val idTokenLifespan: Duration = config.getDuration("service.idTokenLifespan")
    override val masterJsonWebKeySet: JsonWebKeySet = JsonWebKeySet(config.getString("service.jwks"))
    override val nonceEntropy: Int = config.getInt("service.nonceEntropy")
    override val issuerUrl: String = discovery.issuer
    override val authorizeEndpointUrl: String = discovery.authorizationEndpoint
    override val tokenEndpointUrl: String = discovery.tokenEndpoint
    override val defaultTokenEndpointAuthenticationMethod: String = AuthenticationMethod.clientSecretBasic
    override val authorizeCodeLifespan: Duration = Duration.ZERO
    override val accessTokenLifespan: Duration = config.getDuration("service.accessTokenLifespan")
    override val refreshTokenLifespan: Duration = Duration.ZERO
    override val stateEntropy: Int = config.getInt("service.stateEntropy")

    override fun validate() { super<OidcContext>.validate() }
}

object NoOpAccessTokenRepository : AccessTokenRepository {
    override suspend fun createAccessTokenSession(token: String, request: OAuthRequest) {}
    override suspend fun getAccessTokenSession(token: String): OAuthRequest = shouldNotBeCalled()
    override suspend fun deleteAccessTokenSession(token: String) {}
    override suspend fun deleteAccessTokenAssociatedWithRequest(requestId: String) {}
}

class LocalJsonWebKeySetStrategy(serverJwks: JsonWebKeySet) : JsonWebKeySetStrategy(
    httpClient = object : SimpleHttpClient { override suspend fun get(url: String): HttpResponse = shouldNotBeCalled() },
    jsonWebKeySetRepository = object : JsonWebKeySetRepository {
        override suspend fun getServerJsonWebKeySet(): JsonWebKeySet = serverJwks
        override suspend fun getClientJsonWebKeySet(jwksUri: String): JsonWebKeySet? = null
        override suspend fun writeClientJsonWebKeySet(jwksUri: String, keySet: JsonWebKeySet) {}
    }
)