package io.imulab.astrea.service.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.imulab.astrea.sdk.client.Client
import io.imulab.astrea.service.client.handlers.CreateClientHandler
import io.imulab.astrea.service.client.handlers.clientModule
import io.imulab.astrea.service.client.support.ClientDbJsonSupport
import io.imulab.astrea.service.client.verticle.ClientApiVerticle
import io.vertx.core.Vertx
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.awaitResult
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

suspend fun main() {
    val vertx = Vertx.vertx()
    val components = wireComponents(vertx)

    try {
        val clientApi: ClientApiVerticle by components.instance()
        awaitResult<String> {
            vertx.deployVerticle(clientApi, it)
        }
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

fun wireComponents(vertx: Vertx): Kodein {
    val jsonModule = Kodein.Module("json") {
        bind<ObjectMapper>(tag = "apiMapper") with singleton {
            ObjectMapper().apply {
                propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
                registerModule(clientModule)
                registerModule(KotlinModule())
            }
        }
        bind<ObjectMapper>(tag = "dbMapper") with singleton {
            ObjectMapper().apply {
                registerModule(KotlinModule())
                registerModule(SimpleModule().apply {
                    addSerializer(Client::class.java, ClientDbJsonSupport.serializer)
                    addDeserializer(Client::class.java, ClientDbJsonSupport.deserializer)
                })
            }
        }
    }

    val dbModule = Kodein.Module("db") {
        bind<MongoClient>() with singleton {
            MongoClient.createNonShared(vertx, json {
                obj(
                    "host" to "localhost",
                    "port" to 32768
                )
            })
        }
    }

    val handlerModule = Kodein.Module("handler") {
        importOnce(jsonModule)
        importOnce(dbModule)

        bind<CreateClientHandler>() with singleton {
            CreateClientHandler(
                mongoClient = instance(),
                apiMapper = instance("apiMapper"),
                dbMapper = instance("dbMapper")
            )
        }
    }

    return Kodein {
        importOnce(handlerModule)

        bind<ClientApiVerticle>() with singleton {
            ClientApiVerticle(
                createClientHandler = instance()
            )
        }
    }
}
