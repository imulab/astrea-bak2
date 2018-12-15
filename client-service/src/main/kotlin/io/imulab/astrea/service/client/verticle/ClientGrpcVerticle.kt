package io.imulab.astrea.service.client.verticle

import com.typesafe.config.Config
import io.imulab.astrea.service.client.grpc.ClientAuthenticationService
import io.imulab.astrea.service.client.grpc.ClientLookupService
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.grpc.VertxServerBuilder
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class ClientGrpcVerticle(
    private val appConfig: Config,
    private val clientLookupService: ClientLookupService,
    private val clientAuthenticationService: ClientAuthenticationService
) : AbstractVerticle() {

    private val logger = LoggerFactory.getLogger(ClientGrpcVerticle::class.java)

    override fun start(startFuture: Future<Void>?) {
        val server = VertxServerBuilder
            .forPort(vertx, appConfig.getInt("service.grpcPort"))
            .addService(clientLookupService)
            .addService(clientAuthenticationService)
            .build()

        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            logger.info("ClientGrpcVerticle shutting down...")
            server.shutdown()
            server.awaitTermination(10, TimeUnit.SECONDS)
        })

        server.start { ar ->
            if (ar.failed()) {
                logger.error("ClientGrpcVerticle failed to start.", ar.cause())
            } else {
                startFuture?.complete()
                logger.info("ClientGrpcVerticle started...")
            }
        }
    }
}