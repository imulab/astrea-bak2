package io.imulab.astrea.service.proxy.filters.consent

import com.netflix.zuul.context.RequestContext
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.jwk.JwtVerificationKeyResolver
import io.imulab.astrea.service.proxy.ConsentToken
import org.jose4j.jwk.JsonWebKeySet
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ConsentAcquireFilter : ConsentFilter() {

    private val logger = LoggerFactory.getLogger(ConsentAcquireFilter::class.java)

    @Value("\${consent.url}") var consentServiceUrl: String = ""
    @Value("\${service.name}") var serviceName: String = ""

    @Autowired
    @Qualifier("consentProviderJwks")
    lateinit var consentProviderJwks: JsonWebKeySet

    companion object {
        const val ConsentClaims = "ConsentClaims"
    }

    override fun shouldFilter(): Boolean {
        return super.shouldFilter() && hasConsentToken()
    }

    override fun run(): Any {
        val context = RequestContext.getCurrentContext()

        try {
            val claims = JwtConsumerBuilder()
                .also { b ->
                    b.setRequireJwtId()
                    b.setRequireExpirationTime()
                    b.setExpectedIssuer(consentServiceUrl)
                    b.setExpectedAudience(serviceName)
                    b.setVerificationKeyResolver(JwtVerificationKeyResolver(consentProviderJwks, JwtSigningAlgorithm.RS256))
                }
                .build()
                .processToClaims(context.requestQueryParams[ConsentToken]!![0]!!)

            context.set(ConsentClaims, claims)

            setAcquired()
        } catch (e: Exception) {
            logger.debug("Verification encountered error, consent assumed to have not been obtained.", e)
            context.set(ConsentClaims, JwtClaims())
        }

        return Unit
    }

    override fun filterOrder(): Int = BaseOrder

    private fun hasConsentToken() = RequestContext.getCurrentContext().containsKey(ConsentToken)
}