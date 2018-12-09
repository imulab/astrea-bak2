package io.imulab.astrea.service.authorization.config

import io.imulab.astrea.service.authorization.authz.consent.*
import io.imulab.astrea.service.authorization.authz.repo.OidcAuthorizeRequestRepository
import io.imulab.astrea.service.authorization.http.SpringWebSessionStrategy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

/**
 * This class configures all authorization/consent related components (minus state management).
 */
@Configuration
class ConsentConfiguration @Autowired constructor(
    private val properties: NixProperties,
    private val environment: Environment,
    private val oidcAuthorizeRequestRepository: OidcAuthorizeRequestRepository
) {

    @Bean
    fun consentTokenStrategy() = ConsentTokenStrategy(
        oidcContext = properties,
        tokenAudience = properties.endpoints.consent,
        tokenLifespan = properties.consentToken.expiration
    )

    @Bean
    fun consentProvider(consentTokenStrategy: ConsentTokenStrategy) = ConsentProvider(
        handlers = mutableListOf(
            ConsentTokenConsentHandler(consentTokenStrategy, SpringWebSessionStrategy),
            SessionConsentHandler(SpringWebSessionStrategy)
        ).apply {
            if (environment.getProperty("nix.security.autoConsent", Boolean::class.java, false))
                add(AutoGrantConsentHandler())
        },
        requestRepository = oidcAuthorizeRequestRepository,
        oidcContext = properties,
        consentTokenStrategy = consentTokenStrategy,
        requireAuthentication = true,
        consentProviderEndpoint = properties.endpoints.consent
    )
}