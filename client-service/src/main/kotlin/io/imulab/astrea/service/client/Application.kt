package io.imulab.astrea.service.client

import io.imulab.astrea.sdk.oauth.OAuthContext
import io.imulab.astrea.sdk.oauth.client.pwd.BCryptPasswordEncoder
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oauth.token.strategy.JwtAccessTokenStrategy
import io.imulab.astrea.sdk.oidc.jwk.JsonWebKeySetRepository
import io.imulab.astrea.sdk.oidc.jwk.JsonWebKeySetStrategy
import io.imulab.astrea.service.client.common.HttpClient
import org.jose4j.jwk.JsonWebKeySet
import org.jose4j.jwk.RsaJwkGenerator
import org.jose4j.jwk.Use
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import java.time.Duration

const val clientScope = "astrea.client"

@SpringBootApplication
@EnableWebMvc
@EnableMongoRepositories
class Application {

    @Autowired
    lateinit var serviceContext: ServiceContext

    @Bean
    fun jsonWebKeySetStrategy() = JsonWebKeySetStrategy(
        jsonWebKeySetRepository = object : JsonWebKeySetRepository {
            override suspend fun getServerJsonWebKeySet(): JsonWebKeySet { throw UnsupportedOperationException() }
            override suspend fun getClientJsonWebKeySet(jwksUri: String): JsonWebKeySet? { return null }
            override suspend fun writeClientJsonWebKeySet(jwksUri: String, keySet: JsonWebKeySet) {}
        },
        httpClient = HttpClient
    )

    @Bean
    fun jwtAccessTokenStrategy() = JwtAccessTokenStrategy(
        oauthContext = serviceContext,
        signingAlgorithm = JwtSigningAlgorithm.RS256,
        // todo should read this from vault.
        serverJwks = JsonWebKeySet().also { s ->
            s.addJsonWebKey(RsaJwkGenerator.generateJwk(2048).also { k ->
                k.keyId = "dd999f81-35d3-47f4-960b-6c1204aeb328"
                k.use = Use.SIGNATURE
                k.algorithm = JwtSigningAlgorithm.RS256.algorithmIdentifier
            })
        }
    )

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}

@Configuration
@ConfigurationProperties("service")
class ServiceContext: OAuthContext {

    // service.issuerUrl
    override var issuerUrl: String = ""
    // service.accessTokenLifespan
    override var accessTokenLifespan: Duration = Duration.ZERO

    // we don't care about these
    override val authorizeEndpointUrl: String = ""
    override val tokenEndpointUrl: String = ""
    override val defaultTokenEndpointAuthenticationMethod: String = ""
    override val authorizeCodeLifespan: Duration = Duration.ZERO
    override val refreshTokenLifespan: Duration = Duration.ZERO
    override val stateEntropy: Int = 0
}

//fun main(args: Array<String>) {
//    runApplication<Application>(*args)
//}