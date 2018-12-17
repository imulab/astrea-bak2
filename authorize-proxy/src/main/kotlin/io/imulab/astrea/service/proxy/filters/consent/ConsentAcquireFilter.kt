package io.imulab.astrea.service.proxy.filters.consent

import com.netflix.zuul.context.RequestContext
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.jwk.JwtVerificationKeyResolver
import io.imulab.astrea.sdk.oidc.jwk.authTime
import io.imulab.astrea.sdk.oidc.jwk.scopes
import io.imulab.astrea.sdk.oidc.jwk.toLocalDateTime
import io.imulab.astrea.service.proxy.*
import org.jose4j.jwk.JsonWebKeySet
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
class ConsentAcquireFilter : ConsentFilter() {

    private val logger = LoggerFactory.getLogger(ConsentAcquireFilter::class.java)

    @Value("\${consent.url}") var consentServiceUrl: String = ""
    @Value("\${service.name}") var serviceName: String = ""

    @Autowired
    @Qualifier("consentProviderJwks")
    lateinit var consentProviderJwks: JsonWebKeySet

    @Autowired
    lateinit var sessionRepository: SessionRepositoryAdapter

    override fun shouldFilter(): Boolean {
        return super.shouldFilter() && hasConsentToken()
    }

    override fun run(): Any {
        acquireConsentFromToken()
        saveToSessionIfRequired()
        return Unit
    }

    private fun acquireConsentFromToken() {
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

            setConsentClaims(claims)
        } catch (e: Exception) {
            logger.debug("Verification encountered error, consent assumed to have not been obtained.", e)
            setConsentClaims(JwtClaims())
        } finally {
            context.requestQueryParams.remove(ConsentToken)
        }
    }

    private fun saveToSessionIfRequired() {
        val claims = getConsentClaims() ?: return
        val rememberForDuration = claims.rememberFor()
        if (!rememberForDuration.isZero) {
            val session = sessionRepository.createSession().apply {
                subject = claims.subject
                consentScope = claims.scopes()
                consentClaims = claims.getUserClaims()
            }
            sessionRepository.save(session)
            // todo set cookie header
        }
    }

    override fun filterOrder(): Int = BaseOrder + 10

    private fun hasConsentToken() = RequestContext.getCurrentContext().containsKey(ConsentToken)
}