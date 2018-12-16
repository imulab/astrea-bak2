package io.imulab.astrea.service.proxy.login

import com.netflix.zuul.context.RequestContext
import io.imulab.astrea.sdk.oauth.error.AccessDenied
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.jwk.JwtVerificationKeyResolver
import org.jose4j.jwk.JsonWebKeySet
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.jose4j.keys.AesKey
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class LoginVerificationFilter : LoginFilter() {

    private val logger = LoggerFactory.getLogger(LoginVerificationFilter::class.java)

    /**
     * The (base) url for the external login provider. It is used as the JWT audience
     * as well as the base url for redirection.
     */
    @Value("\${login.url}")
    var loginServiceUrl: String = ""

    /**
     * Service name for this proxy. It is used as the JWT issuer.
     */
    @Value("\${service.name}")
    var serviceName: String = ""

    /**
     * Base64 encoded secret key, used as the signing key for HS256 algorithm.
     */
    @Value("\${service.signingKey}")
    var signingKey: String = ""

    /**
     * Login provider's Json Web Key Set, used to verify login_token signature.
     */
    @Autowired
    @Qualifier("loginProviderJwks")
    lateinit var loginProviderJwks: JsonWebKeySet

    companion object {
        const val LoginTokenParam = "login_token"
        const val LoginClaims = "LoginClaims"
    }

    override fun shouldFilter(): Boolean {
        return super.shouldFilter() && hasXNonce() && hasLoginToken()
    }

    override fun run(): Any {
        if (hasBeenTempered())
            throw AccessDenied.byServer("query parameters have been tempered.")

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
                .processToClaims(context.requestQueryParams[LoginTokenParam]!![0]!!)

            context.set(LoginClaims, claims)

            setApproved()
        } catch (e: Exception) {
            logger.debug("Verification encountered error, authentication assumed to have failed.", e)
            throw AccessDenied.byServer("authentication failed.")
        }

        return Unit
    }

    private fun hasBeenTempered(): Boolean {
        return try {
            val originalHash = JwtConsumerBuilder()
                .also { b ->
                    b.setRequireJwtId()
                    b.setRequireExpirationTime()
                    b.setExpectedIssuer(serviceName)
                    b.setExpectedAudience(loginServiceUrl)
                    b.setVerificationKey(AesKey(Base64.getDecoder().decode(signingKey)))
                }
                .build()
                .processToClaims(
                    RequestContext.getCurrentContext().requestQueryParams[XNonceParam]!![0]!!
                )
                .getStringClaimValue(LoginRedirectionFilter.RequestHashClaim)

            hashRequestQueryParams { it.key == XNonceParam || it.key == LoginTokenParam } == originalHash
        } catch (e: Exception) {
            logger.debug("Verification encountered error, request assumed to have been tempered.", e)
            true
        }
    }

    override fun filterOrder(): Int = 0

    private fun hasLoginToken() = RequestContext.getCurrentContext().containsKey(LoginTokenParam)
}