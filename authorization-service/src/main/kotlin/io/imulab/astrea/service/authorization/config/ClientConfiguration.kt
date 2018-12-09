package io.imulab.astrea.service.authorization.config

import io.imulab.astrea.sdk.oauth.client.ClientLookup
import io.imulab.astrea.sdk.oauth.client.authn.ClientAuthenticators
import io.imulab.astrea.sdk.oauth.client.authn.ClientSecretBasicAuthenticator
import io.imulab.astrea.sdk.oauth.client.authn.ClientSecretPostAuthenticator
import io.imulab.astrea.sdk.oauth.client.pwd.BCryptPasswordEncoder
import io.imulab.astrea.sdk.oauth.reserved.AuthenticationMethod
import io.imulab.astrea.sdk.oidc.client.MemoryClientStorage
import io.imulab.astrea.sdk.oidc.client.authn.PrivateKeyJwtAuthenticator
import io.imulab.astrea.sdk.oidc.jwk.JsonWebKeySetStrategy
import io.imulab.astrea.service.authorization.config.NixProperties
import io.imulab.astrea.service.authorization.config.Stock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class ClientConfiguration @Autowired constructor(
    private val properties: NixProperties,
    private val jsonWebKeySetStrategy: JsonWebKeySetStrategy
) {

    @Bean @Profile("memory")
    fun memoryClientLookup() = MemoryClientStorage(mutableMapOf("foo" to Stock.clientFoo))

    @Bean
    fun clientAuthenticator(clientLookup: ClientLookup) = ClientAuthenticators(
        authenticators = listOf(
            ClientSecretBasicAuthenticator(clientLookup, BCryptPasswordEncoder()),
            ClientSecretPostAuthenticator(clientLookup, BCryptPasswordEncoder()),
            PrivateKeyJwtAuthenticator(clientLookup, jsonWebKeySetStrategy, properties)
        ),
        defaultMethod = AuthenticationMethod.clientSecretPost
    )
}