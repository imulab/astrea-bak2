package io.imulab.astrea.service.authorization.route

import io.imulab.astrea.sdk.oauth.assertType
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.imulab.astrea.sdk.oauth.handler.AccessRequestHandler
import io.imulab.astrea.sdk.oauth.request.OAuthAccessRequest
import io.imulab.astrea.sdk.oauth.request.OAuthRequestProducer
import io.imulab.astrea.sdk.oauth.response.OAuthResponse
import io.imulab.astrea.sdk.oauth.validation.OAuthRequestValidationChain
import io.imulab.astrea.sdk.oidc.request.OidcRequestForm
import io.imulab.astrea.sdk.oidc.response.OidcTokenEndpointResponse
import kotlinx.coroutines.runBlocking
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class TokenRouteProvider(
    private val requestProducer: OAuthRequestProducer,
    private val validator: OAuthRequestValidationChain,
    private val handlers: List<AccessRequestHandler>
) {

    fun handle(request: ServerRequest): Mono<ServerResponse> {
        val form = Mono.just(request)
            .flatMap { it.formData() }
            .map { OidcRequestForm(it) }

        val parsedRequest = form.map {
            runBlocking {
                requestProducer.produce(it).assertType<OAuthAccessRequest>()
            }
        }

        val validated = parsedRequest.map { it.apply { validator.validate(this) } }

        val handled: Mono<OAuthResponse> = validated
            .map {
                runBlocking {
                    val response = OidcTokenEndpointResponse()
                    for (h in handlers)
                        h.updateSession(it)
                    for (h in handlers)
                        h.handleAccessRequest(it, response)
                    return@runBlocking response
                }
            }

        return handled
            .onErrorResume { t -> Mono.just(t.asOAuthResponse()) }
            .flatMap { resp -> resp.render() }
    }

    private fun OAuthResponse.render(): Mono<ServerResponse> {
        return ServerResponse.status(status).also {
            headers.forEach { t, u -> it.header(t, u) }
        }.syncBody(data)
    }

    private fun Throwable.asOAuthResponse(): OAuthResponse {
        return when (this) {
            is OAuthResponse -> this
            else -> ServerError.wrapped(this)
        }
    }
}