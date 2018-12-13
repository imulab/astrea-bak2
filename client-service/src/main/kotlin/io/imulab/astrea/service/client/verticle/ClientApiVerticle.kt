package io.imulab.astrea.service.client.verticle

import io.imulab.astrea.service.client.handlers.CreateClientHandler
import io.imulab.astrea.service.client.handlers.ReadClientHandler
import io.imulab.astrea.service.client.handlers.errorHandler
import io.imulab.astrea.service.client.support.addSuspendHandlerByOperationId
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.api.contract.RouterFactoryOptions
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import kotlinx.coroutines.cancel

class ClientApiVerticle(
    private val createClientHandler: CreateClientHandler,
    private val readClientHandler: ReadClientHandler
) : CoroutineVerticle() {

    override suspend fun start() {
        val router = OpenAPI3RouterFactory.createAwait(vertx, "client-api-schema.yml").apply {
            options = RouterFactoryOptions().apply {
                isRequireSecurityHandlers = false
                isMountValidationFailureHandler = false
            }

            addFailureHandlerByOperationId("client.create", errorHandler)
            addFailureHandlerByOperationId("client.read", errorHandler)

            addSuspendHandlerByOperationId("client.create", createClientHandler::createClient)
            addSuspendHandlerByOperationId("client.read", readClientHandler::readClient)
        }.router

        vertx.createHttpServer(HttpServerOptions().apply {
            host = "localhost"
            port = 8080
        }).requestHandler(router).listenAwait()
    }

    override suspend fun stop() {
        coroutineContext.cancel()
    }
}