package io.imulab.astrea.service.proxy.auth.client

import io.grpc.Channel
import io.grpc.ManagedChannelBuilder
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.imulab.astrea.sdk.client.DummyClientLookupService
import io.imulab.astrea.sdk.client.GrpcClientLookup
import io.imulab.astrea.sdk.oauth.client.ClientLookup
import io.imulab.astrea.sdk.oauth.client.authn.ClientAuthenticators
import io.imulab.astrea.sdk.oauth.client.authn.ClientSecretBasicAuthenticator
import io.imulab.astrea.sdk.oauth.client.authn.ClientSecretPostAuthenticator
import io.imulab.astrea.sdk.oauth.client.pwd.BCryptPasswordEncoder
import io.imulab.astrea.sdk.oauth.reserved.AuthenticationMethod
import io.imulab.astrea.sdk.oidc.client.OidcClient
import io.imulab.astrea.sdk.oidc.client.authn.OidcClientAuthenticators
import io.imulab.astrea.sdk.oidc.client.authn.PrivateKeyJwtAuthenticator
import io.imulab.astrea.sdk.oidc.jwk.JsonWebKeySetRepository
import io.imulab.astrea.sdk.oidc.jwk.JsonWebKeySetStrategy
import io.imulab.astrea.sdk.oidc.spi.HttpResponse
import io.imulab.astrea.sdk.oidc.spi.SimpleHttpClient
import org.jose4j.jwk.JsonWebKeySet
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/**
 * Configured a remote (or local dummy) grpc [ClientLookup] service.
 */
@Configuration
class ClientServiceConfiguration(private val serviceProperties: ClientServiceProperties) {

    @Bean
    fun clientService() : ClientLookup {
        val channel = ManagedChannelBuilder.forAddress(
            serviceProperties.host,
            serviceProperties.port
        ).enableRetry().maxRetryAttempts(10).usePlaintext().build()

        return GrpcClientLookup(channel)
    }

    @Bean
    @Profile("dummy")
    @Primary
    fun dummyClientService(): ClientLookup {
        InProcessServerBuilder.forName(DummyClientLookupService.serviceName)
            .directExecutor()
            .addService(DummyClientLookupService)
            .build().also {
                Runtime.getRuntime().addShutdownHook(thread(start = false) {
                    it.shutdown()
                    it.awaitTermination(5, TimeUnit.SECONDS)
                })
            }
            .start()
        val channel: Channel = InProcessChannelBuilder.forName(DummyClientLookupService.serviceName)
            .directExecutor()
            .build()
        return GrpcClientLookup(channel)
    }
}

/**
 * Configures
 */
@Configuration
class ClientAuthenticatorConfiguration(private val clientLookup: ClientLookup) {

    @Bean
    fun jsonWebKeySetStrategy(): JsonWebKeySetStrategy {
        val oops: () -> Nothing = { throw UnsupportedOperationException("should not be invoked.") }
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

    @Bean
    fun clientAuthenticators(strategy: JsonWebKeySetStrategy) = OidcClientAuthenticators(
        authenticators = listOf(
            ClientSecretBasicAuthenticator(clientLookup, BCryptPasswordEncoder()),
            ClientSecretPostAuthenticator(clientLookup, BCryptPasswordEncoder())
            //PrivateKeyJwtAuthenticator(clientLookup, strategy, properties)
        // todo we need to alter the constructor signature of private key jwt auth, pass tokenEndpointUrl directly, also obtain this information from discovery
        ),
        clientLookup = clientLookup
    )
}

@Configuration
@ConfigurationProperties("client")
class ClientServiceProperties {
    var host: String = ""
    var port: Int = 0
}