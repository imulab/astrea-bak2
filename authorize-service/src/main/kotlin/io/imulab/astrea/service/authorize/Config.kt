package io.imulab.astrea.service.authorize

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import io.grpc.ManagedChannelBuilder
import io.imulab.astrea.sdk.client.GrpcClientLookup
import io.imulab.astrea.sdk.discovery.GrpcDiscoveryService
import io.imulab.astrea.sdk.oauth.client.ClientLookup
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.imulab.astrea.sdk.oauth.handler.OAuthAuthorizeCodeHandler
import io.imulab.astrea.sdk.oauth.handler.OAuthImplicitHandler
import io.imulab.astrea.sdk.oauth.handler.helper.AccessTokenHelper
import io.imulab.astrea.sdk.oauth.handler.helper.RefreshTokenHelper
import io.imulab.astrea.sdk.oauth.request.OAuthRequestProducer
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oauth.token.strategy.AccessTokenStrategy
import io.imulab.astrea.sdk.oauth.token.strategy.AuthorizeCodeStrategy
import io.imulab.astrea.sdk.oauth.token.strategy.HmacSha2AuthorizeCodeStrategy
import io.imulab.astrea.sdk.oauth.token.strategy.JwtAccessTokenStrategy
import io.imulab.astrea.sdk.oidc.discovery.Discovery
import io.imulab.astrea.sdk.oidc.handler.OidcAuthorizeCodeHandler
import io.imulab.astrea.sdk.oidc.handler.OidcHybridHandler
import io.imulab.astrea.sdk.oidc.handler.OidcImplicitHandler
import io.imulab.astrea.sdk.oidc.request.OidcAuthorizeRequestProducer
import io.imulab.astrea.sdk.oidc.token.IdTokenStrategy
import io.imulab.astrea.sdk.oidc.token.JwxIdTokenStrategy
import io.imulab.astrea.sdk.oidc.validation.OidcResponseTypeValidator
import io.imulab.astrea.service.authorize.verticle.AuthorizeApiVerticle
import io.vavr.control.Try
import io.vertx.core.Vertx
import io.vertx.kotlin.redis.pingAwait
import io.vertx.redis.RedisClient
import io.vertx.redis.RedisOptions
import kotlinx.coroutines.runBlocking
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import java.time.Duration

fun components(vertx: Vertx) : Kodein {

    val app = Kodein.Module("app") {

        bind<Config>() with singleton { ConfigFactory.load() }

        bind<Discovery>() with eagerSingleton {
            Retry.decorateSupplier(
                Retry.of("discovery", RetryConfig.Builder()
                    .maxAttempts(5)
                    .waitDuration(Duration.ofSeconds(10))
                    .retryExceptions(Exception::class.java)
                    .build()
                )
            ) {
                runBlocking {
                    GrpcDiscoveryService(
                        ManagedChannelBuilder.forAddress(
                            instance<Config>().getString("discoveryService.host"),
                            instance<Config>().getInt("discoveryService.port")
                        ).enableRetry().maxRetryAttempts(10).usePlaintext().build()
                    ).getDiscovery()
                }.also {
                    rootLogger.info("Acquired discovery configuration.")
                }
            }.let {
                Try.ofSupplier(it).getOrElse { throw ServerError.internal("Cannot obtain discovery.") }
            }
        }

        bind<ServiceContext>() with singleton {
            ServiceContext(instance(), instance())
        }

        bind<RedisClient>() with singleton {
            val config = instance<Config>()
            RedisClient.create(vertx, RedisOptions().apply {
                host = config.getString("redis.host")
                port = config.getInt("redis.port")
                select = config.getInt("redis.db")
            }).also {
                runBlocking { it.pingAwait() }
            }
        }

        bind<ClientLookup>() with singleton {
            val config = instance<Config>()
            GrpcClientLookup(
                channel = ManagedChannelBuilder
                    .forAddress(
                        config.getString("clientService.host"),
                        config.getInt("clientService.port")
                    )
                    .enableRetry()
                    .maxRetryAttempts(10)
                    .usePlaintext()
                    .build()
            )
        }
    }

    val oidc = Kodein.Module("oidc") {
        importOnce(app)

        // todo: support request objects

        bind<OAuthRequestProducer>(tag = OidcAuthorizeRequestProducer::class.java) with singleton {
            OidcAuthorizeRequestProducer(
                lookup = instance(),
                claimConverter = JacksonClaimConverter,
                responseTypeValidator = OidcResponseTypeValidator
            )
        }

        bind<RedisAuthorizeCodeRepository>() with singleton {
            RedisAuthorizeCodeRepository(instance())
        }

        bind<AuthorizeCodeStrategy>() with singleton {
            HmacSha2AuthorizeCodeStrategy(
                key = instance<ServiceContext>().authorizeCodeKey,
                signingAlgorithm = JwtSigningAlgorithm.HS256
            )
        }

        bind<AccessTokenStrategy>() with singleton {
            JwtAccessTokenStrategy(
                oauthContext = instance(),
                signingAlgorithm = JwtSigningAlgorithm.RS256,
                serverJwks = instance<ServiceContext>().masterJsonWebKeySet
            )
        }

        bind<IdTokenStrategy>() with singleton {
            JwxIdTokenStrategy(
                oidcContext = instance(),
                jsonWebKeySetStrategy = LocalJsonWebKeySetStrategy(instance<ServiceContext>().masterJsonWebKeySet)
            )
        }

        bind<AccessTokenHelper>() with singleton {
            AccessTokenHelper(
                oauthContext = instance(),
                accessTokenRepository = NoOpAccessTokenRepository,
                accessTokenStrategy = instance()
            )
        }

        bind<OAuthAuthorizeCodeHandler>() with singleton {
            OAuthAuthorizeCodeHandler(
                authorizeCodeRepository = instance(),
                authorizeCodeStrategy = instance(),
                accessTokenHelper = instance(),
                refreshTokenHelper = RefreshTokenHelper(
                    refreshTokenRepository = NotImplementedRefreshTokenRepository,
                    refreshTokenStrategy = NotImplementedRefreshTokenStrategy
                )
            )
        }

        bind<OAuthImplicitHandler>() with singleton {
            OAuthImplicitHandler(
                oauthContext = instance(),
                accessTokenStrategy = instance(),
                accessTokenRepository = NoOpAccessTokenRepository
            )
        }

        bind<OidcAuthorizeCodeHandler>() with singleton {
            OidcAuthorizeCodeHandler(
                idTokenStrategy = instance(),
                oidcSessionRepository = instance()
            )
        }

        bind<OidcImplicitHandler>() with singleton {
            OidcImplicitHandler(
                idTokenStrategy = instance(),
                accessTokenHelper = instance()
            )
        }

        bind<OidcHybridHandler>() with singleton {
            OidcHybridHandler(
                oidcAuthorizeCodeHandler = instance(),
                accessTokenHelper = instance(),
                idTokenStrategy = instance(),
                oidcSessionRepository = instance(),
                authorizeCodeStrategy = instance(),
                authorizeCodeRepository = instance()
            )
        }
    }

    return Kodein {
        importOnce(app)
        importOnce(oidc)

        bind<AuthorizeApiVerticle>() with singleton {
            val config = instance<Config>()
            AuthorizeApiVerticle(
                apiPort = config.getInt("service.port"),
                requestProducer = instance(tag = OidcAuthorizeRequestProducer::class.java),
                handlers = listOf(
                    instance<OAuthAuthorizeCodeHandler>(),
                    instance<OAuthImplicitHandler>(),
                    instance<OidcAuthorizeCodeHandler>(),
                    instance<OidcImplicitHandler>(),
                    instance<OidcHybridHandler>()
                )
            )
        }
    }
}