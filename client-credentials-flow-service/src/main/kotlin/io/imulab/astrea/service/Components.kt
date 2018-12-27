package io.imulab.astrea.service

import com.typesafe.config.Config
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import io.grpc.ManagedChannelBuilder
import io.imulab.astrea.sdk.discovery.GrpcDiscoveryService
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.imulab.astrea.sdk.oauth.handler.OAuthClientCredentialsHandler
import io.imulab.astrea.sdk.oauth.handler.helper.AccessTokenHelper
import io.imulab.astrea.sdk.oauth.handler.helper.RefreshTokenHelper
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oauth.token.strategy.HmacSha2RefreshTokenStrategy
import io.imulab.astrea.sdk.oauth.token.strategy.JwtAccessTokenStrategy
import io.imulab.astrea.sdk.oauth.validation.OAuthGrantTypeValidator
import io.imulab.astrea.sdk.oauth.validation.OAuthRequestValidationChain
import io.imulab.astrea.sdk.oauth.validation.ScopeValidator
import io.imulab.astrea.sdk.oidc.discovery.Discovery
import io.vavr.control.Try
import io.vertx.core.Vertx
import kotlinx.coroutines.runBlocking
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
            importOnce(app)

            bind<GrpcVerticle>() with singleton { GrpcVerticle(instance(), config) }
        }
    }

    val app = Kodein.Module("app") {
        bind<ServiceContext>() with singleton {
            ServiceContext(instance(), config)
        }

        bind<OAuthClientCredentialsHandler>() with singleton {
            OAuthClientCredentialsHandler(
                accessTokenHelper = AccessTokenHelper(
                    oauthContext = instance(),
                    accessTokenStrategy = JwtAccessTokenStrategy(
                        oauthContext = instance(),
                        signingAlgorithm = JwtSigningAlgorithm.RS256,
                        serverJwks = instance<ServiceContext>().masterJsonWebKeySet
                    ),
                    accessTokenRepository = NoOpAccessTokenRepository
                ),
                refreshTokenHelper = RefreshTokenHelper(
                    refreshTokenStrategy = HmacSha2RefreshTokenStrategy(
                        key = instance<ServiceContext>().refreshTokenKey,
                        signingAlgorithm = JwtSigningAlgorithm.HS256
                    ),
                    refreshTokenRepository = NoOpRefreshTokenRepository
                )
            )
        }

        bind<ClientCredentialsFlowService>() with singleton {
            ClientCredentialsFlowService(
                handlers = listOf(
                    instance<OAuthClientCredentialsHandler>()
                ),
                exchangeValidation = OAuthRequestValidationChain(listOf(
                    ScopeValidator,
                    OAuthGrantTypeValidator
                ))
            )
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
}