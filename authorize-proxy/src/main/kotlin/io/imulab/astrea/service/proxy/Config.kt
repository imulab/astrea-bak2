package io.imulab.astrea.service.proxy

import org.jose4j.jwk.JsonWebKeySet
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProxyConfiguration {

    @Bean("loginProviderJwks")
    fun loginProviderJwks(@Value("\${login.service.jwks}") jwksJson: String) = jwksJson.asJwks()

    @Bean("consentProviderJwks")
    fun consentProviderJwks(@Value("\${consent.service.jwks}") jwksJson: String) = jwksJson.asJwks()

    @Bean("authorizeProviderJwks")
    fun authorizeProviderJwks(@Value("\${authorize.service.jwks}") jwksJson: String) = jwksJson.asJwks()

    private fun String.asJwks(): JsonWebKeySet =
        takeIf { it.isNotEmpty() }?.let { JsonWebKeySet(it) } ?: JsonWebKeySet()
}