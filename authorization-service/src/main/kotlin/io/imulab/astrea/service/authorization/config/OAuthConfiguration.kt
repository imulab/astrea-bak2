package io.imulab.astrea.service.authorization.config

import io.imulab.astrea.sdk.oauth.handler.OAuthAuthorizeCodeHandler
import io.imulab.astrea.sdk.oauth.handler.OAuthClientCredentialsHandler
import io.imulab.astrea.sdk.oauth.handler.OAuthImplicitHandler
import io.imulab.astrea.sdk.oauth.handler.OAuthRefreshHandler
import io.imulab.astrea.sdk.oauth.handler.helper.AccessTokenHelper
import io.imulab.astrea.sdk.oauth.handler.helper.RefreshTokenHelper
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oauth.token.storage.*
import io.imulab.astrea.sdk.oauth.token.strategy.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OAuthConfiguration @Autowired constructor(private val prop: NixProperties) {

    @Bean
    @Memory
    fun memoryAuthorizeCodeRepository() = MemoryAuthorizeCodeRepository()

    @Bean
    @Memory
    fun memoryAccessTokenRepository() = MemoryAccessTokenRepository()

    @Bean
    @Memory
    fun memoryRefreshTokenRepository() = MemoryRefreshTokenRepository()

    @Bean
    fun authorizeCodeStrategy() =
        HmacSha2AuthorizeCodeStrategy(prop.authorizeCode.key.toAesKey(), JwtSigningAlgorithm.HS256)

    @Bean
    fun accessTokenStrategy() = JwtAccessTokenStrategy(prop, JwtSigningAlgorithm.RS256, prop.masterJsonWebKeySet)

    @Bean
    fun refreshTokenStrategy() =
        HmacSha2RefreshTokenStrategy(prop.refreshToken.key.toAesKey(), JwtSigningAlgorithm.HS256)

    @Bean
    fun accessTokenHelper(strategy: AccessTokenStrategy, repo: AccessTokenRepository) =
        AccessTokenHelper(prop, strategy, repo)

    @Bean
    fun refreshTokenHelper(strategy: RefreshTokenStrategy, repo: RefreshTokenRepository) =
        RefreshTokenHelper(strategy, repo)

    @Bean
    fun oauthAuthorizeCodeHandler(
        authorizeCodeStrategy: AuthorizeCodeStrategy,
        authorizeCodeRepository: AuthorizeCodeRepository,
        accessTokenHelper: AccessTokenHelper,
        refreshTokenHelper: RefreshTokenHelper
    ) = OAuthAuthorizeCodeHandler(
        authorizeCodeStrategy,
        authorizeCodeRepository,
        accessTokenHelper,
        refreshTokenHelper
    )

    @Bean
    fun oauthImplicitHandler(accessTokenStrategy: AccessTokenStrategy, accessTokenRepository: AccessTokenRepository) =
        OAuthImplicitHandler(prop, accessTokenStrategy, accessTokenRepository)

    @Bean
    fun oauthClientCredentialsHandler(accessTokenHelper: AccessTokenHelper, refreshTokenHelper: RefreshTokenHelper) =
        OAuthClientCredentialsHandler(accessTokenHelper, refreshTokenHelper)

    @Bean
    fun oauthRefreshHandler(
        accessTokenHelper: AccessTokenHelper,
        refreshTokenHelper: RefreshTokenHelper,
        accessTokenRepository: AccessTokenRepository,
        refreshTokenStrategy: RefreshTokenStrategy,
        refreshTokenRepository: RefreshTokenRepository
    ) = OAuthRefreshHandler(
        accessTokenHelper,
        refreshTokenHelper,
        accessTokenRepository,
        refreshTokenStrategy,
        refreshTokenRepository
    )
}