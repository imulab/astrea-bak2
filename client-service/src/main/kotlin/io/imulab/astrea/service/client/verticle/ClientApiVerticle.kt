package io.imulab.astrea.service.client.verticle

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.imulab.astrea.service.client.handlers.addSuspendHandlerByOperationId
import io.imulab.astrea.service.client.handlers.clientModule
import io.imulab.astrea.service.client.handlers.createClient
import io.imulab.astrea.service.client.handlers.errorHandler
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.Json
import io.vertx.ext.web.api.contract.RouterFactoryOptions
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import kotlinx.coroutines.cancel

class ClientApiVerticle : CoroutineVerticle() {

    override suspend fun start() {
        Json.mapper.apply {
            propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
            registerModule(clientModule)
            registerModule(KotlinModule())
        }
        Json.prettyMapper.apply {
            propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
            registerModule(clientModule)
            registerModule(KotlinModule())
        }

        val router = OpenAPI3RouterFactory.createAwait(vertx, "client-api-schema.yml").apply {
            options = RouterFactoryOptions().apply {
                isRequireSecurityHandlers = false
                isMountValidationFailureHandler = false
            }
            addFailureHandlerByOperationId("client.create", errorHandler)
            addSuspendHandlerByOperationId("client.create", ::createClient)
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