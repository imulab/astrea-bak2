package io.imulab.astrea.service.client.verticle

import io.imulab.astrea.service.client.grpc.ClientLookupService
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.grpc.VertxServerBuilder
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class ClientGrpcVerticle(private val clientLookupService: ClientLookupService) : AbstractVerticle() {

    override fun start(startFuture: Future<Void>?) {
        val server = VertxServerBuilder
            .forAddress(vertx, "localhost", 35027)
            .addService(clientLookupService)
            .build()

        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            server.shutdown()
            server.awaitTermination(10, TimeUnit.SECONDS)
        })

        server.start(startFuture?.completer())
    }
}