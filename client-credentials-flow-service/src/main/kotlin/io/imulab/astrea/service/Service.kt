package io.imulab.astrea.service

import com.typesafe.config.Config
import io.grpc.stub.StreamObserver
import io.imulab.astrea.flow.cc.toAccessRequest
import io.imulab.astrea.flow.cc.toClientCredentialsTokenResponse
import io.imulab.astrea.flow.cc.toFailureResponse
import io.imulab.astrea.sdk.flow.cc.ClientCredentialsFlowGrpc
import io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenRequest
import io.imulab.astrea.sdk.flow.cc.ClientCredentialsTokenResponse
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.imulab.astrea.sdk.oauth.handler.AccessRequestHandler
import io.imulab.astrea.sdk.oauth.response.TokenEndpointResponse
import io.imulab.astrea.sdk.oauth.validation.OAuthRequestValidationChain
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.grpc.VertxServerBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext

class GrpcVerticle(
    private val flowService: ClientCredentialsFlowService,
    private val appConfig: Config
) : AbstractVerticle() {

    private val logger = LoggerFactory.getLogger(GrpcVerticle::class.java)

    override fun start(startFuture: Future<Void>?) {
        val server = VertxServerBuilder
            .forPort(vertx, appConfig.getInt("service.port"))
            .addService(flowService)
            .build()

        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            logger.info("GrpcVerticle shutting down...")
            server.shutdown()
            server.awaitTermination(10, TimeUnit.SECONDS)
        })

        server.start { ar ->
            if (ar.failed()) {
                logger.error("GrpcVerticle failed to start.", ar.cause())
            } else {
                startFuture?.complete()
                logger.info("GrpcVerticle started...")
            }
        }
    }
}

class ClientCredentialsFlowService(
    private val concurrency: Int = 4,
    override val coroutineContext: CoroutineContext = Executors.newFixedThreadPool(concurrency).asCoroutineDispatcher(),
    private val handlers: List<AccessRequestHandler>,
    private val exchangeValidation: OAuthRequestValidationChain
) : ClientCredentialsFlowGrpc.ClientCredentialsFlowImplBase(), CoroutineScope {

    override fun exchange(
        request: ClientCredentialsTokenRequest?,
        responseObserver: StreamObserver<ClientCredentialsTokenResponse>?
    ) {
        if (request == null || responseObserver == null)
            return

        val job = Job()
        val accessRequest = request.toAccessRequest()
        val accessResponse = TokenEndpointResponse()

        launch(job) {
            exchangeValidation.validate(accessRequest)

            handlers.forEach { h -> h.updateSession(accessRequest) }
            handlers.forEach { h -> h.handleAccessRequest(accessRequest, accessResponse) }
        }.invokeOnCompletion { t ->
            if (t != null) {
                job.cancel()
                val e = if (t is OAuthException) t else ServerError.wrapped(t)
                responseObserver.onNext(e.toFailureResponse())
            } else {
                responseObserver.onNext(accessResponse.toClientCredentialsTokenResponse())
            }
            responseObserver.onCompleted()
        }
    }
}