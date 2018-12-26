package io.imulab.astrea.service

import com.typesafe.config.Config
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import io.grpc.ManagedChannelBuilder
import io.imulab.astrea.sdk.discovery.GrpcDiscoveryService
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.imulab.astrea.sdk.oauth.handler.OAuthImplicitHandler
import io.imulab.astrea.sdk.oauth.handler.helper.AccessTokenHelper
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oauth.token.strategy.AccessTokenStrategy
import io.imulab.astrea.sdk.oauth.token.strategy.JwtAccessTokenStrategy
import io.imulab.astrea.sdk.oauth.validation.*
import io.imulab.astrea.sdk.oidc.discovery.Discovery
import io.imulab.astrea.sdk.oidc.handler.OidcImplicitHandler
import io.imulab.astrea.sdk.oidc.token.IdTokenStrategy
import io.imulab.astrea.sdk.oidc.token.JwxIdTokenStrategy
import io.imulab.astrea.sdk.oidc.validation.NonceValidator
import io.imulab.astrea.sdk.oidc.validation.OidcResponseTypeValidator
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

            bind<GrpcVerticle>() with singleton {
                GrpcVerticle(flowService = instance(), appConfig = config)
            }
        }
    }

    val app = Kodein.Module("app") {
        bind<ServiceContext>() with singleton { ServiceContext(discovery = instance(), config = config) }

        bind<AccessTokenStrategy>() with singleton {
            JwtAccessTokenStrategy(
                oauthContext = instance(),
                serverJwks = instance<ServiceContext>().masterJsonWebKeySet,
                signingAlgorithm = JwtSigningAlgorithm.RS256
            )
        }

        bind<AccessTokenHelper>() with singleton {
            AccessTokenHelper(
                oauthContext = instance(),
                accessTokenRepository = NoOpAccessTokenRepository,
                accessTokenStrategy = instance()
            )
        }

        bind<IdTokenStrategy>() with singleton {
            JwxIdTokenStrategy(
                oidcContext = instance(),
                jsonWebKeySetStrategy = LocalJsonWebKeySetStrategy(instance<ServiceContext>().masterJsonWebKeySet)
            )
        }

        bind<OAuthImplicitHandler>() with singleton {
            OAuthImplicitHandler(
                oauthContext = instance(),
                accessTokenStrategy = instance(),
                accessTokenRepository = NoOpAccessTokenRepository
            )
        }

        bind<OidcImplicitHandler>() with singleton {
            OidcImplicitHandler(
                accessTokenHelper = instance(),
                idTokenStrategy = instance()
            )
        }

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

        bind<ImplicitFlowService>() with singleton {
            ImplicitFlowService(
                authorizeHandlers = listOf(
                    instance<OAuthImplicitHandler>(),
                    instance<OidcImplicitHandler>()
                ),
                authorizeValidation = instance()
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