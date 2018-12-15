package io.imulab.astrea.service.proxy.config

import io.grpc.ManagedChannelBuilder
import io.imulab.astrea.sdk.client.GrpcClientBasicAuthenticator
import io.imulab.astrea.sdk.client.GrpcClientLookup
import io.imulab.astrea.sdk.client.GrpcClientPostAuthenticator
import io.imulab.astrea.sdk.oauth.OAuthContext
import io.imulab.astrea.sdk.oauth.client.ClientLookup
import io.imulab.astrea.sdk.oauth.reserved.AuthenticationMethod
import io.imulab.astrea.sdk.oidc.client.OidcClient
import io.imulab.astrea.sdk.oidc.client.authn.OidcClientAuthenticators
import io.imulab.astrea.sdk.oidc.client.authn.PrivateKeyJwtAuthenticator
import io.imulab.astrea.sdk.oidc.discovery.Discovery
import io.imulab.astrea.sdk.oidc.jwk.JsonWebKeySetRepository
import io.imulab.astrea.sdk.oidc.jwk.JsonWebKeySetStrategy
import io.imulab.astrea.sdk.oidc.spi.HttpResponse
import io.imulab.astrea.sdk.oidc.spi.SimpleHttpClient
import org.jose4j.jwk.JsonWebKeySet
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class ClientServiceConfiguration(private val serviceProperties: ClientServiceProperties) {

    private fun channel() = ManagedChannelBuilder.forAddress(
        serviceProperties.host,
        serviceProperties.port
    ).enableRetry().maxRetryAttempts(10).usePlaintext().build()

    @Bean
    fun clientLookup() = GrpcClientLookup(channel())

    @Bean
    fun clientAuthenticators(clientLookup: ClientLookup, discovery: Discovery) = OidcClientAuthenticators(
        authenticators = listOf(
            GrpcClientBasicAuthenticator(channel()),
            GrpcClientPostAuthenticator(channel()),
            PrivateKeyJwtAuthenticator(
                clientLookup = clientLookup,
                clientJwksStrategy = jsonWebKeySetStrategy(),
                oauthContext = object : OAuthContext {
                    override val issuerUrl: String
                        get() = discovery.issuer
                    override val authorizeEndpointUrl: String
                        get() = discovery.authorizationEndpoint
                    override val tokenEndpointUrl: String
                        get() = discovery.tokenEndpoint
                    override val defaultTokenEndpointAuthenticationMethod: String
                        get() = AuthenticationMethod.clientSecretBasic
                    override val authorizeCodeLifespan: Duration
                        get() = oops()
                    override val accessTokenLifespan: Duration
                        get() = oops()
                    override val refreshTokenLifespan: Duration
                        get() = oops()
                    override val stateEntropy: Int
                        get() = oops()
                }
            )
            // todo we need to alter the constructor signature of private key jwt auth, pass tokenEndpointUrl directly, also obtain this information from discovery
        ),
        clientLookup = clientLookup
    )

    private fun jsonWebKeySetStrategy(): JsonWebKeySetStrategy {
        val httpClient = object : SimpleHttpClient { override suspend fun get(url: String): HttpResponse { oops() } }
        val repo = object : JsonWebKeySetRepository {
            override suspend fun getServerJsonWebKeySet(): JsonWebKeySet { oops() }
            override suspend fun getClientJsonWebKeySet(jwksUri: String): JsonWebKeySet? { oops() }
            override suspend fun writeClientJsonWebKeySet(jwksUri: String, keySet: JsonWebKeySet) { oops() }
        }

        return object : JsonWebKeySetStrategy(repo, httpClient) {
            override suspend fun resolveKeySet(client: OidcClient): JsonWebKeySet {
                if (client.jwks.isEmpty())
                    return JsonWebKeySet()
                return JsonWebKeySet(client.jwks)
            }
        }
    }

    private val oops: () -> Nothing = { throw UnsupportedOperationException("should not be invoked.") }
}

@Configuration
@ConfigurationProperties("client")
class ClientServiceProperties {
    var host: String = ""
    var port: Int = 0
}