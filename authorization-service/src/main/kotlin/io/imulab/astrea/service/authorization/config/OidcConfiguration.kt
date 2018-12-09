package io.imulab.astrea.service.authorization.config

import io.imulab.astrea.sdk.oauth.handler.helper.AccessTokenHelper
import io.imulab.astrea.sdk.oauth.token.storage.AuthorizeCodeRepository
import io.imulab.astrea.sdk.oauth.token.strategy.AuthorizeCodeStrategy
import io.imulab.astrea.sdk.oidc.handler.OidcAuthorizeCodeHandler
import io.imulab.astrea.sdk.oidc.handler.OidcHybridHandler
import io.imulab.astrea.sdk.oidc.handler.OidcImplicitHandler
import io.imulab.astrea.sdk.oidc.handler.OidcRefreshHandler
import io.imulab.astrea.sdk.oidc.jwk.JsonWebKeySetRepository
import io.imulab.astrea.sdk.oidc.jwk.JsonWebKeySetStrategy
import io.imulab.astrea.sdk.oidc.request.MemoryOidcSessionRepository
import io.imulab.astrea.sdk.oidc.request.OidcSessionRepository
import io.imulab.astrea.sdk.oidc.token.IdTokenStrategy
import io.imulab.astrea.sdk.oidc.token.JwxIdTokenStrategy
import io.imulab.astrea.service.authorization.http.SpringHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * This class configures everything related to OIDC.
 */
@Configuration
class OidcConfiguration @Autowired constructor(
    private val properties: NixProperties,
    private val accessTokenHelper: AccessTokenHelper,
    private val authorizeCodeStrategy: AuthorizeCodeStrategy,
    private val authorizeCodeRepository: AuthorizeCodeRepository
) {

    @Bean @Memory
    fun oidcSessionRepository() = MemoryOidcSessionRepository()

    @Bean
    fun jwksStrategy(repo: JsonWebKeySetRepository) = JsonWebKeySetStrategy(repo, SpringHttpClient)

    @Bean
    fun idTokenStrategy(strategy: JsonWebKeySetStrategy) = JwxIdTokenStrategy(properties, strategy)

    @Bean
    fun oidcAuthorizeCodeHandler(strategy: IdTokenStrategy, repo: OidcSessionRepository) =
        OidcAuthorizeCodeHandler(strategy, repo)

    @Bean
    fun oidcImplicitHandler(strategy: IdTokenStrategy) = OidcImplicitHandler(accessTokenHelper, strategy)

    @Bean
    fun oidcHybridHandler(
        authHandler: OidcAuthorizeCodeHandler,
        idTokenStrategy: IdTokenStrategy,
        sessionRepository: OidcSessionRepository
    ) = OidcHybridHandler(
        authHandler,
        authorizeCodeStrategy,
        authorizeCodeRepository,
        idTokenStrategy,
        accessTokenHelper,
        sessionRepository
    )

    @Bean
    fun oidcRefreshHandler(idTokenStrategy: IdTokenStrategy) = OidcRefreshHandler(idTokenStrategy)
}