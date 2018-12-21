package io.imulab.astrea.service

import com.typesafe.config.Config
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import io.grpc.ManagedChannelBuilder
import io.imulab.astrea.sdk.discovery.GrpcDiscoveryService
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.imulab.astrea.sdk.oauth.handler.OAuthAuthorizeCodeHandler
import io.imulab.astrea.sdk.oauth.handler.helper.AccessTokenHelper
import io.imulab.astrea.sdk.oauth.handler.helper.RefreshTokenHelper
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oauth.token.storage.AccessTokenRepository
import io.imulab.astrea.sdk.oauth.token.storage.RefreshTokenRepository
import io.imulab.astrea.sdk.oauth.token.strategy.*
import io.imulab.astrea.sdk.oauth.validation.*
import io.imulab.astrea.sdk.oidc.discovery.Discovery
import io.imulab.astrea.sdk.oidc.handler.OidcAuthorizeCodeHandler
import io.imulab.astrea.sdk.oidc.token.IdTokenStrategy
import io.imulab.astrea.sdk.oidc.token.JwxIdTokenStrategy
import io.imulab.astrea.sdk.oidc.validation.NonceValidator
import io.imulab.astrea.sdk.oidc.validation.OidcResponseTypeValidator
import io.vavr.control.Try
import io.vertx.core.Vertx
import io.vertx.redis.RedisClient
import io.vertx.redis.RedisOptions
import kotlinx.coroutines.runBlocking
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import java.time.Duration

open class Components(vertx: Vertx, private val config: Config) {

    open fun bootstrap(): Kodein {
        return Kodein {
            importOnce(discovery)
            importOnce(persistence)
            importOnce(validation)
            importOnce(app)

            bind<AuthorizeCodeFlowService>() with singleton {
                AuthorizeCodeFlowService(
                    authorizeHandlers = listOf(
                        instance<OAuthAuthorizeCodeHandler>(),
                        instance<OidcAuthorizeCodeHandler>()
                    ),
                    exchangeHandlers = listOf(
                        instance<OAuthAuthorizeCodeHandler>(),
                        instance<OidcAuthorizeCodeHandler>()
                    ),
                    redisAuthorizeCodeRepository = instance(),
                    validation = instance()
                )
            }

            bind<GrpcVerticle>() with singleton { GrpcVerticle(instance(), config) }
        }
    }

    val discovery = Kodein.Module("discovery") {
        bind<Discovery>() with eagerSingleton {
            val channel = ManagedChannelBuilder.forAddress(
                config.getString("discovery.host"),
                config.getInt("discovery.port")
            ).enableRetry().maxRetryAttempts(10).usePlaintext().build()

            val retry = Retry.of(
                "discovery", RetryConfig.Builder()
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

            Try.ofSupplier(discovery)
                .getOrElse { throw ServerError.internal("Cannot obtain discovery.") }
        }
    }

    val persistence = Kodein.Module("persistence") {
        bind<RedisAuthorizeCodeRepository>() with singleton {
            RedisAuthorizeCodeRepository(
                RedisClient.create(vertx, RedisOptions().apply {
                    host = config.getString("redis.host")
                    port = config.getInt("redis.port")
                    select = config.getInt("redis.db")
                }),
                instance<ServiceContext>().authorizeCodeLifespan
            )
        }

        bind<RefreshTokenRepository>() with singleton { PublishingRefreshTokenRepository() }
    }

    val validation = Kodein.Module("validation") {
        bind<OAuthRequestValidationChain>() with singleton {
            OAuthRequestValidationChain(listOf(
                StateValidator(instance()),
                NonceValidator(instance()),
                ScopeValidator,
                GrantedScopeValidator,
                RedirectUriValidator,
                OidcResponseTypeValidator
            ))
        }
    }

    val app = Kodein.Module("app") {
        bind<ServiceContext>() with singleton {
            ServiceContext(
                config,
                instance()
            )
        }

        bind<AuthorizeCodeStrategy>() with singleton {
            HmacSha2AuthorizeCodeStrategy(
                instance<ServiceContext>().authorizeCodeKey,
                JwtSigningAlgorithm.HS256
            ).enableServiceAware(config.getString("service.id"))
        }

        bind<AccessTokenStrategy>() with singleton {
            JwtAccessTokenStrategy(
                instance<ServiceContext>(),
                JwtSigningAlgorithm.RS256,
                instance<ServiceContext>().masterJsonWebKeySet
            )
        }

        bind<AccessTokenRepository>() with singleton { NoOpAccessTokenRepository }

        bind<RefreshTokenStrategy>() with singleton {
            HmacSha2RefreshTokenStrategy(
                instance<ServiceContext>().refreshTokenKey,
                JwtSigningAlgorithm.HS256
            )
        }

        bind<IdTokenStrategy>() with singleton {
            JwxIdTokenStrategy(
                instance<ServiceContext>(),
                LocalJsonWebKeySetStrategy(instance<ServiceContext>().masterJsonWebKeySet)
            )
        }

        bind<AccessTokenHelper>() with singleton { AccessTokenHelper(instance(), instance(), instance()) }

        bind<RefreshTokenHelper>() with singleton { RefreshTokenHelper(instance(), instance()) }

        bind<OAuthAuthorizeCodeHandler>() with singleton {
            OAuthAuthorizeCodeHandler(
                authorizeCodeStrategy = instance(),
                authorizeCodeRepository = instance(),
                accessTokenHelper = instance(),
                refreshTokenHelper = instance()
            )
        }

        bind<OidcAuthorizeCodeHandler>() with singleton {
            OidcAuthorizeCodeHandler(
                idTokenStrategy = instance(),
                oidcSessionRepository = instance()
            )
        }
    }
}
