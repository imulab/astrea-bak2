package io.imulab.astrea.service.client.common

import io.imulab.astrea.sdk.oidc.spi.HttpResponse
import io.imulab.astrea.sdk.oidc.spi.SimpleHttpClient
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.ResponseEntity
import java.time.Duration

object HttpClient : SimpleHttpClient {

    private val template = RestTemplateBuilder()
        .setConnectTimeout(Duration.ofSeconds(10))
        .setReadTimeout(Duration.ofSeconds(10))
        .build()

    override suspend fun get(url: String): HttpResponse {
        return SimpleHttpResponse(template.getForEntity(url, String::class.java))
    }

    class SimpleHttpResponse(private val entity: ResponseEntity<String>) : HttpResponse {

        override fun status(): Int = entity.statusCode.value()

        override fun body(): String = entity.body ?: ""
    }
}