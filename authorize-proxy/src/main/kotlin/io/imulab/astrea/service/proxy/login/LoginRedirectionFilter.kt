package io.imulab.astrea.service.proxy.login

import com.netflix.zuul.context.RequestContext
import io.imulab.astrea.sdk.oauth.error.AccessDenied
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import okhttp3.HttpUrl
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.keys.AesKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

/**
 * A [LoginFilter] to be placed at the end of the login filter chain. This filter is the last resort
 * of login processing as all other login filter failed to determine authentication status. Hence,
 * we redirect to the external login provider here.
 *
 * The request parameters are forwarded to the login provider and is expected to be sent back. This ensures that
 * the proxy does not have to endure database round trips to cache them, and thus gaining performance benefits.
 *
 * To ensure the login provider does not somehow rewrite the query parameters. An 'x_nonce' parameter is calculated
 * as a JWT containing base64 encoded SHA-256 hash of the query parameters, and is expected to be send back along
 * with the login response. Another filter, on receiving the login response, should validate the integrity of the
 * query parameters to ensure exactly the original request parameters plus the login response and x_nonce is sent
 * back.
 */
@Component
class LoginRedirectionFilter : LoginFilter() {

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

    companion object {
        const val RequestHashClaim = "req_hash"
    }

    override fun run(): Any {
        // Assume authentication failure when x_nonce parameter is already present
        // It means we have received response from login provider but still failed
        // to establish authentication status.
        if (hasXNonce())
            throw AccessDenied.byServer("user authentication failed.")

        val nonce = JsonWebSignature().also { jws ->
            jws.payload = JwtClaims().also { c ->
                c.setGeneratedJwtId()
                c.setIssuedAtToNow()
                c.setExpirationTimeMinutesInTheFuture(10f)
                c.setAudience(loginServiceUrl)
                c.issuer = serviceName
                c.setClaim(RequestHashClaim, hashRequestQueryParams())
            }.toJson()
            jws.key = AesKey(Base64.getDecoder().decode(signingKey))
            jws.algorithmHeaderValue = JwtSigningAlgorithm.HS256.algorithmIdentifier
        }.compactSerialization

        RequestContext.getCurrentContext().run {
            setSendZuulResponse(false)
            requestQueryParams[XNonceParam] = listOf(nonce)
            response.sendRedirect(
                HttpUrl.parse(loginServiceUrl)!!
                    .newBuilder()
                    .also { b ->
                        requestQueryParams
                            .flatMap { e -> e.value.map { v -> e.key to v } }
                            .forEach { p -> b.addQueryParameter(p.first, p.second) }
                    }
                    .build().toString()
            )
        }

        return Unit
    }

    override fun filterOrder(): Int = 99
}