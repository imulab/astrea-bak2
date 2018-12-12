package io.imulab.astrea.service.client.handlers

import io.imulab.astrea.sdk.oauth.error.InvalidRequest
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.vertx.core.Handler
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.validation.ValidationException
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

val errorHandler: Handler<RoutingContext> = Handler { rc ->
    val ex = rc.failure()
    when (ex) {
        is OAuthException -> {
            rc.response().apply {
                statusCode = ex.status
                ex.headers.forEach { t, u -> putHeader(t, u) }
                applicationJson(ex.data)
            }
        }
        is ValidationException -> {
            rc.response().apply {
                statusCode = InvalidRequest.status
                applicationJson {
                    json {
                        obj(
                            "error" to InvalidRequest.code,
                            "error_description" to ex.message
                        )
                    }
                }
            }
        }
        else -> {
            rc.response().apply {
                statusCode = ServerError.status
                applicationJson {
                    json {
                        obj(
                            "error" to ServerError.code,
                            "error_description" to ex.message
                        )
                    }
                }
            }
        }
    }
}