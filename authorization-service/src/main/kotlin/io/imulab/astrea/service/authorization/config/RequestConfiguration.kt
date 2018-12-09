package io.imulab.astrea.service.authorization.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.imulab.astrea.sdk.oauth.client.ClientLookup
import io.imulab.astrea.sdk.oauth.client.authn.ClientAuthenticators
import io.imulab.astrea.sdk.oauth.validation.OAuthGrantTypeValidator
import io.imulab.astrea.sdk.oidc.jwk.JsonWebKeySetStrategy
import io.imulab.astrea.sdk.oidc.request.*
import io.imulab.astrea.sdk.oidc.validation.OidcResponseTypeValidator
import io.imulab.astrea.service.authorization.authz.ResumeOidcAuthorizeRequestProducer
import io.imulab.astrea.service.authorization.authz.repo.MemoryOidcAuthorizeRequestRepository
import io.imulab.astrea.service.authorization.authz.repo.OidcAuthorizeRequestRepository
import io.imulab.astrea.service.authorization.http.SpringHttpClient
import io.imulab.astrea.service.authorization.oidc.JacksonClaimConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * This class configures everything related to an OAuth/Oidc request (minus state management).
 */
@Configuration
class RequestConfiguration @Autowired constructor(
    private val properties: NixProperties,
    private val jsonWebKeySetStrategy: JsonWebKeySetStrategy,
    private val clientLookup: ClientLookup,
    private val clientAuthenticators: ClientAuthenticators,
    private val objectMapper: ObjectMapper
) {

    @Bean @Memory
    fun memoryOidcAuthorizeRequestRepository() = MemoryOidcAuthorizeRequestRepository()

    @Bean @Memory
    fun memoryCachedRequestRepository() = MemoryCachedRequestRepository()

    @Bean
    fun requestStrategy(repository: CachedRequestRepository) = RequestStrategy(
        repository = repository,
        httpClient = SpringHttpClient,
        jsonWebKeySetStrategy = jsonWebKeySetStrategy,
        serverContext = properties
    )

    @Bean("authorizeRequestProducer")
    fun authorizeRequestProducer(arRepo: OidcAuthorizeRequestRepository, requestStrategy: RequestStrategy) =
        ResumeOidcAuthorizeRequestProducer(
            oidcAuthorizeRequestRepository = arRepo,
            defaultProducer = RequestObjectAwareOidcAuthorizeRequestProducer(
                discovery = properties,
                requestStrategy = requestStrategy,
                firstPassProducer = OidcAuthorizeRequestProducer(
                    lookup = clientLookup,
                    claimConverter = JacksonClaimConverter(objectMapper),
                    responseTypeValidator = OidcResponseTypeValidator
                )
            )
        )

    @Bean("accessRequestProducer")
    fun accessRequestProducer() = OidcAccessRequestProducer(
        grantTypeValidator = OAuthGrantTypeValidator,
        clientAuthenticators = clientAuthenticators
    )
}