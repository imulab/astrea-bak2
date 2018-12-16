package io.imulab.astrea.service.proxy.filters.login

import com.netflix.zuul.context.RequestContext
import io.imulab.astrea.sdk.oauth.client.ClientLookup
import io.imulab.astrea.sdk.oauth.reserved.Param
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.client.OidcClient
import io.imulab.astrea.sdk.oidc.discovery.Discovery
import io.imulab.astrea.sdk.oidc.jwk.JwtVerificationKeyResolver
import io.imulab.astrea.sdk.oidc.jwk.authTime
import io.imulab.astrea.sdk.oidc.reserved.OidcParam
import kotlinx.coroutines.runBlocking
import org.jose4j.jwk.JsonWebKeySet
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class LoginHintFilter : LoginFilter() {

    private val logger = LoggerFactory.getLogger(LoginHintFilter::class.java)

    @Autowired
    lateinit var clientLookup: ClientLookup

    @Autowired
    lateinit var discovery: Discovery

    @Autowired @Qualifier("authorizeProviderJwks")
    lateinit var authorizeProviderJwks: JsonWebKeySet

    override fun shouldFilter(): Boolean {
        return super.shouldFilter() && hasIdTokenHint()
    }

    override fun run(): Any {
        try {
            val client = getClient() ?: return Unit
            val idTokenHint = RequestContext.getCurrentContext().requestQueryParams[OidcParam.idTokenHint]?.get(0) ?: ""

            val tokenClaims = verifyTokenSignature(idTokenHint, client)

            // ensure authentication represented by id_token_hint has not expired beyond max_age yet.
            val authTimeClaim = tokenClaims.authTime() ?: return Unit
            val maxAge = if (getMaxAge() > 0) getMaxAge() else client.defaultMaxAge
            if (maxAge > 0 && authTimeClaim.plusSeconds(maxAge).isBefore(LocalDateTime.now()))
                return Unit

            setLoginClaims(tokenClaims)
        } catch (e: Exception) {
            logger.debug("Id token hint verification encountered error, silently skipping...", e)
        }

        return Unit
    }

    override fun filterOrder(): Int = BaseOrder + 20

    private fun getClient(): OidcClient? {
        val clientId = RequestContext.getCurrentContext().requestQueryParams[Param.clientId]?.get(0) ?: ""
        if (clientId.isEmpty())
            return null
        return runBlocking { clientLookup.find(clientId) } as? OidcClient
    }

    private fun getMaxAge(): Long {
        return RequestContext.getCurrentContext().requestQueryParams[OidcParam.maxAge]?.get(0)?.toLong() ?: 0
    }

    private fun verifyTokenSignature(token: String, client: OidcClient): JwtClaims {
        return when (client.idTokenSignedResponseAlgorithm) {
            JwtSigningAlgorithm.None -> {
                JwtConsumerBuilder()
                    .setRequireJwtId()
                    .setJwsAlgorithmConstraints(client.idTokenSignedResponseAlgorithm.whitelisted())
                    .setSkipVerificationKeyResolutionOnNone()
                    .setDisableRequireSignature()
                    .setExpectedIssuer(discovery.issuer)
                    .build()
                    .processToClaims(token)
            }
            else -> {
                JwtConsumerBuilder()
                    .setRequireJwtId()
                    .setJwsAlgorithmConstraints(client.idTokenSignedResponseAlgorithm.whitelisted())
                    .setVerificationKeyResolver(
                        JwtVerificationKeyResolver(
                            authorizeProviderJwks,
                            client.idTokenSignedResponseAlgorithm
                        )
                    )
                    .setExpectedIssuer(discovery.issuer)
                    .build()
                    .processToClaims(token)
            }
        }
    }

    private fun hasIdTokenHint(): Boolean =
        RequestContext.getCurrentContext().requestQueryParams.containsKey(OidcParam.idTokenHint)
}