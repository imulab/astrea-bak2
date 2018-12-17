package io.imulab.astrea.service.proxy.filters.login

import com.netflix.zuul.context.RequestContext
import io.imulab.astrea.sdk.oauth.error.AccessDenied
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.jwk.JwtVerificationKeyResolver
import io.imulab.astrea.sdk.oidc.jwk.authTime
import io.imulab.astrea.sdk.oidc.jwk.toLocalDateTime
import io.imulab.astrea.service.proxy.*
import org.jose4j.jwk.JsonWebKeySet
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.jose4j.lang.JoseException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.session.Session
import org.springframework.session.SessionRepository
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
class LoginVerificationFilter : LoginFilter() {

    private val logger = LoggerFactory.getLogger(LoginVerificationFilter::class.java)

    @Value("\${login.url}")
    var loginServiceUrl: String = ""

    @Value("\${service.name}")
    var serviceName: String = ""

    @Autowired @Qualifier("loginProviderJwks")
    lateinit var loginProviderJwks: JsonWebKeySet

    @Autowired
    lateinit var sessionRepository: SessionRepositoryAdapter

    override fun shouldFilter(): Boolean {
        return super.shouldFilter() && hasLoginToken()
    }

    override fun run(): Any {
        verifyLoginToken()

        saveToSessionIfRequired()

        return Unit
    }

    private fun verifyLoginToken() {
        val context = RequestContext.getCurrentContext()

        try {
            val claims = JwtConsumerBuilder()
                .also { b ->
                    b.setRequireJwtId()
                    b.setRequireExpirationTime()
                    b.setExpectedIssuer(loginServiceUrl)
                    b.setExpectedAudience(serviceName)
                    b.setVerificationKeyResolver(JwtVerificationKeyResolver(loginProviderJwks, JwtSigningAlgorithm.RS256))
                }
                .build()
                .processToClaims(context.requestQueryParams[LoginToken]!![0]!!)

            if (claims.subject.isEmpty())
                throw AccessDenied.byServer("authentication failed.")

            setLoginClaims(claims)
        } catch (e: JoseException) {
            logger.debug("Verification encountered error, authentication assumed to have failed.", e)
            throw AccessDenied.byServer("authentication failed.")
        } finally {
            context.requestQueryParams.remove(LoginToken)
        }
    }

    private fun saveToSessionIfRequired() {
        val claims = getLoginClaims() ?: return
        val rememberForDuration = claims.rememberFor()
        if (!rememberForDuration.isZero) {
            val session = sessionRepository.createSession().apply {
                subject = claims.subject
                authTime = claims.authTime() ?: claims.issuedAt.toLocalDateTime()
                authenticationExpiry = LocalDateTime.now(ZoneOffset.UTC).plus(rememberForDuration)
            }
            sessionRepository.save(session)
            // todo set cookie header
        }
    }

    override fun filterOrder(): Int = BaseOrder + 10

    private fun hasLoginToken() = RequestContext.getCurrentContext().containsKey(LoginToken)
}