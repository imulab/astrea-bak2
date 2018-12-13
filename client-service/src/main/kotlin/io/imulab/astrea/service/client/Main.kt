package io.imulab.astrea.service.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.imulab.astrea.sdk.client.Client
import io.imulab.astrea.sdk.oauth.client.pwd.BCryptPasswordEncoder
import io.imulab.astrea.service.client.grpc.ClientLookupService
import io.imulab.astrea.service.client.handlers.CreateClientHandler
import io.imulab.astrea.service.client.handlers.ReadClientHandler
import io.imulab.astrea.service.client.support.ClientApiJsonSupport
import io.imulab.astrea.service.client.verticle.ClientApiVerticle
import io.imulab.astrea.service.client.verticle.ClientGrpcVerticle
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.ext.mongo.runCommandAwait
import kotlinx.coroutines.runBlocking
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.slf4j.LoggerFactory

private val rootLogger = LoggerFactory.getLogger("io.imulab.astrea.service.client.Main")

suspend fun main() {

    val vertx = Vertx.vertx(VertxOptions().apply {
        preferNativeTransport = true
    })
    val components = wireComponents(vertx)

    try {
        val clientRest: ClientApiVerticle by components.instance()
        val clientGrpc: ClientGrpcVerticle by components.instance()

        awaitResult<String> { vertx.deployVerticle(clientRest, it) }
            .let { rootLogger.info("Rest API deployed with id {}", it) }

        awaitResult<String> { vertx.deployVerticle(clientGrpc, it) }
            .let { rootLogger.info("GRPC API deployed with id {}", it) }

    } catch (e: Throwable) {
        rootLogger.error("Server encountered error.", e)
    }
}

fun wireComponents(vertx: Vertx): Kodein {
    val appModule = Kodein.Module("app") {
        bind<Config>() with singleton {
            ConfigFactory.load()
        }

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
    }

    val grpcApiModule = Kodein.Module("grpcApi") {
        bind<ClientLookupService>() with singleton {
            ClientLookupService(mongoClient = instance())
        }
    }

    val restApiModule = Kodein.Module("restApi") {
        importOnce(appModule)

        bind<ObjectMapper>() with singleton {
            ObjectMapper().apply {
                propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
                registerModule(KotlinModule())
                registerModule(SimpleModule().apply {
                    addSerializer(Client::class.java, ClientApiJsonSupport.serializer)
                })
            }
        }

        bind<CreateClientHandler>() with singleton {
            CreateClientHandler(
                mongoClient = instance(),
                apiMapper = instance(),
                passwordEncoder = BCryptPasswordEncoder()
            )
        }
        bind<ReadClientHandler>() with singleton {
            ReadClientHandler(
                mongoClient = instance(),
                apiMapper = instance()
            )
        }
    }

    return Kodein {
        importOnce(restApiModule)
        importOnce(grpcApiModule)

        bind<ClientApiVerticle>() with singleton {
            ClientApiVerticle(
                createClientHandler = instance(),
                readClientHandler = instance()
            )
        }

        bind<ClientGrpcVerticle>() with singleton {
            ClientGrpcVerticle(
                clientLookupService = instance()
            )
        }
    }
}