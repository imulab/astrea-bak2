package io.imulab.astrea.service.client.verticle

import com.typesafe.config.Config
import io.imulab.astrea.service.client.grpc.ClientAuthenticationService
import io.imulab.astrea.service.client.grpc.ClientLookupService
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.grpc.VertxServerBuilder
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class ClientGrpcVerticle(
    private val appConfig: Config,
    private val clientLookupService: ClientLookupService,
    private val clientAuthenticationService: ClientAuthenticationService
) : AbstractVerticle() {

    override fun start(startFuture: Future<Void>?) {
        val server = VertxServerBuilder
            .forAddress(vertx, "localhost", appConfig.getInt("service.grpcPort"))
            .addService(clientLookupService)
            .addService(clientAuthenticationService)
            .build()

        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            server.shutdown()
            server.awaitTermination(10, TimeUnit.SECONDS)
        })

        server.start(startFuture?.completer())
    }
}