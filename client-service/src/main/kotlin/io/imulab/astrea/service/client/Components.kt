package io.imulab.astrea.service.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import io.grpc.Channel
import io.grpc.ManagedChannelBuilder
import io.imulab.astrea.sdk.client.Client
import io.imulab.astrea.sdk.discovery.GrpcDiscoveryService
import io.imulab.astrea.sdk.oauth.client.pwd.BCryptPasswordEncoder
import io.imulab.astrea.sdk.oauth.client.pwd.PasswordEncoder
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.imulab.astrea.sdk.oidc.discovery.Discovery
import io.imulab.astrea.service.client.grpc.ClientAuthenticationService
import io.imulab.astrea.service.client.grpc.ClientLookupService
import io.imulab.astrea.service.client.handlers.CreateClientHandler
import io.imulab.astrea.service.client.handlers.ReadClientHandler
import io.imulab.astrea.service.client.support.ClientApiJsonSupport
import io.imulab.astrea.service.client.verticle.ClientApiVerticle
import io.imulab.astrea.service.client.verticle.ClientGrpcVerticle
import io.vavr.control.Try
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.ext.mongo.runCommandAwait
import kotlinx.coroutines.runBlocking
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import java.time.Duration

fun wireComponents(vertx: Vertx): Kodein {

    val appModule = Kodein.Module("app") {

        bind<Config>() with singleton { ConfigFactory.load() }

        bind<MongoClient>() with singleton {
            val client = MongoClient.createNonShared(vertx, json {
                obj(
                    "host" to instance<Config>().getString("mongo.host"),
                    "port" to instance<Config>().getLong("mongo.port"),
                    "db_name" to instance<Config>().getString("mongo.db")
                )
            })

            runBlocking {
                client.runCommandAwait("ping", JsonObject().apply { put("ping", "1") })
            }

            return@singleton client
        }

        bind<Discovery>() with eagerSingleton {
            val channel = ManagedChannelBuilder.forAddress(
                instance<Config>().getString("discovery.host"),
                instance<Config>().getInt("discovery.port")
            ).enableRetry().maxRetryAttempts(10).usePlaintext().build()

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
                    rootLogger.info("Acquired discovery configuration.")
                }
            }

            Try.ofSupplier(discovery).getOrElse { throw ServerError.internal("Cannot obtain discovery.") }
        }

        bind<PasswordEncoder>() with singleton { BCryptPasswordEncoder() }
    }

    val grpcApiModule = Kodein.Module("grpcApi") {
        importOnce(appModule)

        bind<ClientLookupService>() with singleton {
            ClientLookupService(mongoClient = instance())
        }

        bind<ClientAuthenticationService>() with singleton {
            ClientAuthenticationService(mongoClient = instance(), passwordEncoder = instance())
        }
    }

    val restApiModule = Kodein.Module("restApi") {
        importOnce(appModule)

        bind<ObjectMapper>() with singleton {
            ObjectMapper().apply {
                propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
                registerModule(KotlinModule())
                registerModule(SimpleModule().apply {
                    addSerializer(
                        Client::class.java,
                        ClientApiJsonSupport.serializer
                    )
                })
            }
        }

        bind<CreateClientHandler>() with singleton {
            CreateClientHandler(mongoClient = instance(), apiMapper = instance(), passwordEncoder = instance())
        }

        bind<ReadClientHandler>() with singleton {
            ReadClientHandler(mongoClient = instance(), apiMapper = instance())
        }
    }

    return Kodein {
        importOnce(restApiModule)
        importOnce(grpcApiModule)

        bind<ClientApiVerticle>() with singleton {
            ClientApiVerticle(createClientHandler = instance(), readClientHandler = instance(), appConfig = instance())
        }

        bind<ClientGrpcVerticle>() with singleton {
            ClientGrpcVerticle(
                clientLookupService = instance(),
                clientAuthenticationService = instance(),
                appConfig = instance()
            )
        }
    }
}