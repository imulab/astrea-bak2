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
import io.vavr.control.Try
import io.vertx.core.Vertx
import kotlinx.coroutines.runBlocking
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import java.time.Duration

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
                    requestProducer = instance()
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
                            instance<Config>().getString("discovery.host"),
                            instance<Config>().getInt("discovery.port")
                        ).enableRetry().maxRetryAttempts(10).usePlaintext().build()
                    ).getDiscovery()
                }.also {
                    logger.info("Acquired discovery configuration.")
                }
            }.let {
                Try.ofSupplier(it).getOrElse { throw ServerError.internal("Cannot obtain discovery.") }
            }
        }
    }
}