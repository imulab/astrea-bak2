package io.imulab.astrea.service

import com.typesafe.config.Config
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import io.grpc.ManagedChannelBuilder
import io.imulab.astrea.sdk.client.GrpcClientBasicAuthenticator
import io.imulab.astrea.sdk.client.GrpcClientLookup
import io.imulab.astrea.sdk.client.GrpcClientPostAuthenticator
import io.imulab.astrea.sdk.discovery.GrpcDiscoveryService
import io.imulab.astrea.sdk.flow.AuthorizeCodeFlowGrpc
import io.imulab.astrea.sdk.flow.cc.ClientCredentialsFlowGrpc
import io.imulab.astrea.sdk.oauth.OAuthContext
import io.imulab.astrea.sdk.oauth.client.ClientLookup
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.imulab.astrea.sdk.oauth.request.OAuthRequestProducer
import io.imulab.astrea.sdk.oauth.reserved.AuthenticationMethod
import io.imulab.astrea.sdk.oauth.validation.OAuthGrantTypeValidator
import io.imulab.astrea.sdk.oidc.client.OidcClient
import io.imulab.astrea.sdk.oidc.client.authn.OidcClientAuthenticators
import io.imulab.astrea.sdk.oidc.client.authn.PrivateKeyJwtAuthenticator
import io.imulab.astrea.sdk.oidc.discovery.Discovery
import io.imulab.astrea.sdk.oidc.jwk.JsonWebKeySetRepository
import io.imulab.astrea.sdk.oidc.jwk.JsonWebKeySetStrategy
import io.imulab.astrea.sdk.oidc.request.OidcAccessRequestProducer
import io.imulab.astrea.sdk.oidc.spi.HttpResponse
import io.imulab.astrea.sdk.oidc.spi.SimpleHttpClient
import io.imulab.astrea.service.dispatch.AuthorizeCodeFlow
import io.imulab.astrea.service.dispatch.ClientCredentialsFlow
import io.vavr.control.Try
import io.vertx.core.Vertx
import io.vertx.ext.healthchecks.HealthCheckHandler
import kotlinx.coroutines.runBlocking
import org.jose4j.jwk.JsonWebKeySet
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import java.time.Duration

open class Components(private val vertx: Vertx, private val config: Config) {

    open fun bootstrap(): Kodein {
        return Kodein {
            importOnce(discovery)
            importOnce(client)
            importOnce(authorizeCodeFlow)
            importOnce(clientCredentialsFlow)
            importOnce(app)

            bind<GatewayVerticle>() with singleton {
                GatewayVerticle(
                    appConfig = config,
                    healthCheckHandler = instance(),
                    requestProducer = instance(),
                    dispatchers = listOf(
                        instance<AuthorizeCodeFlow.TokenLeg>(),
                        instance<ClientCredentialsFlow>()
                    )
                )
            }
        }
    }

    val app = Kodein.Module("app") {
        bind<HealthCheckHandler>() with singleton { HealthCheckHandler.create(vertx) }

        bind<OidcClientAuthenticators>() with singleton {
            OidcClientAuthenticators(
                authenticators = listOf(
                    instance<GrpcClientBasicAuthenticator>(),
                    instance<GrpcClientPostAuthenticator>(),
                    instance<PrivateKeyJwtAuthenticator>()
                ),
                clientLookup = instance()
            )
        }

        bind<OAuthRequestProducer>() with singleton {
            OidcAccessRequestProducer(
                grantTypeValidator = OAuthGrantTypeValidator,
                clientAuthenticators = instance()
            )
        }
    }

    val authorizeCodeFlow = Kodein.Module("authorizeCodeFlow") {
        bind<AuthorizeCodeFlow.TokenLeg>() with singleton {
            AuthorizeCodeFlow.TokenLeg(
                authorizationCodeFlowPrefix = config.getString("authorizeCodeFlow.serviceId"),
                stub = AuthorizeCodeFlowGrpc.newBlockingStub(
                    ManagedChannelBuilder
                        .forAddress(
                            config.getString("authorizeCodeFlow.host"),
                            config.getInt("authorizeCodeFlow.port")
                        )
                        .enableRetry()
                        .maxRetryAttempts(10)
                        .usePlaintext()
                        .build()
                )
            )
        }
    }

    val clientCredentialsFlow = Kodein.Module("clientCredentialsFlow") {
        bind<ClientCredentialsFlow>() with singleton {
            ClientCredentialsFlow(
                stub = ClientCredentialsFlowGrpc.newBlockingStub(
                    ManagedChannelBuilder
                        .forAddress(
                            config.getString("clientCredentialsFlow.host"),
                            config.getInt("clientCredentialsFlow.port")
                        )
                        .enableRetry()
                        .maxRetryAttempts(10)
                        .usePlaintext()
                        .build()
                )
            )
        }
    }

    val client = Kodein.Module("client") {
        val clientChannel = ManagedChannelBuilder
            .forAddress(
                config.getString("client.host"),
                config.getInt("client.port")
            )
            .enableRetry()
            .maxRetryAttempts(10)
            .usePlaintext()
            .build()

        bind<ClientLookup>() with singleton { GrpcClientLookup(clientChannel) }

        bind<GrpcClientBasicAuthenticator>() with singleton { GrpcClientBasicAuthenticator(clientChannel) }

        bind<GrpcClientPostAuthenticator>() with singleton { GrpcClientPostAuthenticator(clientChannel) }

        bind<PrivateKeyJwtAuthenticator>() with singleton {
            val notImplemented : () -> Nothing = { throw NotImplementedError("This operation should not be called.") }
            PrivateKeyJwtAuthenticator(
                clientLookup = instance(),
                oauthContext = object : OAuthContext {
                    override val issuerUrl: String by lazy { instance<Discovery>().issuer }
                    override val authorizeEndpointUrl: String by lazy { instance<Discovery>().authorizationEndpoint }
                    override val tokenEndpointUrl: String by lazy { instance<Discovery>().tokenEndpoint }
                    override val defaultTokenEndpointAuthenticationMethod: String = AuthenticationMethod.clientSecretBasic
                    override val authorizeCodeLifespan: Duration by lazy { notImplemented() }
                    override val accessTokenLifespan: Duration by lazy { notImplemented() }
                    override val refreshTokenLifespan: Duration by lazy { notImplemented() }
                    override val stateEntropy: Int by lazy { notImplemented() }
                },
                clientJwksStrategy = object : JsonWebKeySetStrategy(
                    httpClient = object : SimpleHttpClient { override suspend fun get(url: String): HttpResponse { notImplemented() } },
                    jsonWebKeySetRepository = object : JsonWebKeySetRepository {
                        override suspend fun getServerJsonWebKeySet(): JsonWebKeySet { notImplemented() }
                        override suspend fun getClientJsonWebKeySet(jwksUri: String): JsonWebKeySet? { notImplemented() }
                        override suspend fun writeClientJsonWebKeySet(jwksUri: String, keySet: JsonWebKeySet) { notImplemented() }
                    }
                ) {
                    override suspend fun resolveKeySet(client: OidcClient): JsonWebKeySet {
                        if (client.jwks.isEmpty())
                            return JsonWebKeySet()
                        return JsonWebKeySet(client.jwks)
                    }
                }
            )
        }
    }

    val discovery = Kodein.Module("discovery") {
        bind<Discovery>() with eagerSingleton {
            val channel = ManagedChannelBuilder
                .forAddress(
                    config.getString("discovery.host"),
                    config.getInt("discovery.port")
                )
                .enableRetry()
                .maxRetryAttempts(10)
                .usePlaintext()
                .build()

            val retry = Retry.of("discovery", RetryConfig.Builder()
                .maxAttempts(5)
                .waitDuration(Duration.ofSeconds(10))
                .retryExceptions(Exception::class.java)
                .build()
            )

            val discovery = Retry.decorateSupplier(retry) {
                runBlocking {
                    GrpcDiscoveryService(channel).getDiscovery()
                }.also {
                    logger.info("Acquired discovery configuration.")
                }
            }

            Try.ofSupplier(discovery).getOrElse { throw ServerError.internal("Cannot obtain discovery.") }
        }
    }
}