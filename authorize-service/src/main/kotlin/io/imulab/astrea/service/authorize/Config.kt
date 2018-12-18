package io.imulab.astrea.service.authorize

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.grpc.ManagedChannelBuilder
import io.imulab.astrea.sdk.client.GrpcClientLookup
import io.imulab.astrea.sdk.oauth.client.ClientLookup
import io.imulab.astrea.service.authorize.verticle.AuthorizeApiVerticle
import io.vertx.core.Vertx
import io.vertx.kotlin.redis.pingAwait
import io.vertx.redis.RedisClient
import io.vertx.redis.RedisOptions
import kotlinx.coroutines.runBlocking
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

fun components(vertx: Vertx) : Kodein {

    val app = Kodein.Module("app") {

        bind<Config>() with singleton { ConfigFactory.load() }

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

    return Kodein {
        importOnce(app)

        bind<AuthorizeApiVerticle>() with singleton {
            val config = instance<Config>()
            AuthorizeApiVerticle(
                apiPort = config.getInt("service.port")
            )
        }
    }
}