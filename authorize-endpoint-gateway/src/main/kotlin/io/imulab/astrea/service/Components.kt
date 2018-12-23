package io.imulab.astrea.service

import com.typesafe.config.Config
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import io.grpc.ManagedChannelBuilder
import io.imulab.astrea.sdk.client.GrpcClientLookup
import io.imulab.astrea.sdk.discovery.GrpcDiscoveryService
import io.imulab.astrea.sdk.oauth.client.ClientLookup
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.imulab.astrea.sdk.oauth.request.OAuthRequestProducer
import io.imulab.astrea.sdk.oidc.discovery.Discovery
import io.imulab.astrea.sdk.oidc.request.OidcAuthorizeRequestProducer
import io.imulab.astrea.sdk.oidc.validation.OidcResponseTypeValidator
import io.imulab.astrea.service.authn.AuthenticationHandler
import io.imulab.astrea.service.authn.IdTokenHintAuthenticationFilter
import io.imulab.astrea.service.authn.LoginTokenAuthenticationFilter
import io.imulab.astrea.service.authz.AuthorizationHandler
import io.imulab.astrea.service.authz.ConsentTokenAuthorizationFilter
import io.imulab.astrea.service.lock.ParameterLocker
import io.vavr.control.Try
import io.vertx.core.Vertx
import kotlinx.coroutines.runBlocking
import org.jose4j.jwk.JsonWebKeySet
import org.jose4j.keys.AesKey
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import java.time.Duration
import java.util.*

open class Components(
    private val vertx: Vertx,
    private val config: Config
) {

    open fun bootstrap(): Kodein {
        return Kodein {
            importOnce(discovery)
            importOnce(client)
            importOnce(app)

            bind<GatewayVerticle>() with singleton {
                GatewayVerticle(
                    appConfig = config,
                    requestProducer = instance(),
                    authenticationHandler = instance(),
                    authorizationHandler = instance(),
                    parameterLocker = instance()
                )
            }
        }
    }

    val app = Kodein.Module("app") {
        bind<OAuthRequestProducer>() with singleton {
            // todo support request object
            OidcAuthorizeRequestProducer(
                lookup = instance(),
                claimConverter = JacksonClaimConverter,
                responseTypeValidator = OidcResponseTypeValidator
            )
        }

        bind<ParameterLocker>() with singleton {
            ParameterLocker(
                serviceName = config.getString("service.name"),
                lockKey = AesKey(Base64.getDecoder().decode(config.getString("service.paramLockKey")))
            )
        }

        bind<AuthenticationHandler>() with singleton {
            AuthenticationHandler(
                loginProviderUrl = config.getString("login.url"),
                filters = listOf(
                    instance<LoginTokenAuthenticationFilter>(),
                    instance<IdTokenHintAuthenticationFilter>()
                ),
                locker = instance()
            )
        }

        bind<LoginTokenAuthenticationFilter>() with singleton {
            LoginTokenAuthenticationFilter(
                loginProviderUrl = config.getString("login.url"),
                serviceName = config.getString("service.name"),
                loginProviderJwks = JsonWebKeySet(config.getString("login.jwks"))
            )
        }

        bind<IdTokenHintAuthenticationFilter>() with singleton {
            IdTokenHintAuthenticationFilter(instance(), JsonWebKeySet(config.getString("service.jwks")))
        }

        bind<ConsentTokenAuthorizationFilter>() with singleton {
            ConsentTokenAuthorizationFilter(
                consentProviderUrl = config.getString("consent.url"),
                serviceName = config.getString("service.name"),
                consentProviderJwks = JsonWebKeySet(config.getString("consent.jwks"))
            )
        }

        bind<AuthorizationHandler>() with singleton {
            AuthorizationHandler(
                consentProviderUrl = config.getString("consent.url"),
                locker = instance(),
                filters = listOf(
                    instance<ConsentTokenAuthorizationFilter>()
                )
            )
        }
    }

    val client = Kodein.Module("client") {
        bind<ClientLookup>() with singleton {
            GrpcClientLookup(
                channel = ManagedChannelBuilder
                    .forAddress(
                        config.getString("client.host"),
                        config.getInt("client.port")
                    )
                    .enableRetry()
                    .maxRetryAttempts(10)
                    .usePlaintext()
                    .build()
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