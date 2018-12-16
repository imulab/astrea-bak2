package io.imulab.astrea.service.proxy.filters.login

import com.netflix.zuul.context.RequestContext
import io.imulab.astrea.sdk.oauth.error.AccessDenied
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.jwk.JwtVerificationKeyResolver
import io.imulab.astrea.service.proxy.LoginToken
import io.imulab.astrea.service.proxy.XNonce
import org.jose4j.jwk.JsonWebKeySet
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.jose4j.lang.JoseException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class LoginVerificationFilter : LoginFilter() {

    private val logger = LoggerFactory.getLogger(LoginVerificationFilter::class.java)

    @Value("\${login.url}") var loginServiceUrl: String = ""
    @Value("\${service.name}") var serviceName: String = ""

    @Autowired @Qualifier("loginProviderJwks")
    lateinit var loginProviderJwks: JsonWebKeySet

    companion object {
        const val LoginClaims = "LoginClaims"
    }

    override fun shouldFilter(): Boolean {
        return super.shouldFilter() && hasLoginToken()
    }

    override fun run(): Any {
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

            context.set(LoginClaims, claims)
            setApproved()
        } catch (e: JoseException) {
            logger.debug("Verification encountered error, authentication assumed to have failed.", e)
            throw AccessDenied.byServer("authentication failed.")
        }

        return Unit
    }

    override fun filterOrder(): Int = BaseOrder

    private fun hasLoginToken() = RequestContext.getCurrentContext().containsKey(LoginToken)
}