package io.imulab.astrea.service.authn

import io.imulab.astrea.sdk.oauth.assertType
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.client.OidcClient
import io.imulab.astrea.sdk.oidc.discovery.Discovery
import io.imulab.astrea.sdk.oidc.jwk.JwtVerificationKeyResolver
import io.imulab.astrea.sdk.oidc.jwk.acrValues
import io.imulab.astrea.sdk.oidc.jwk.authTime
import io.imulab.astrea.sdk.oidc.request.OidcAuthorizeRequest
import io.imulab.astrea.sdk.oidc.request.OidcSession
import io.imulab.astrea.sdk.oidc.validation.AuthTimeValidator
import io.vertx.ext.web.RoutingContext
import org.jose4j.jwk.JsonWebKeySet
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.slf4j.LoggerFactory

/**
 * An [AuthenticationFilter] implementation that attempts to resolve authentication from a previously issued
 * id_token via parameter id_token_hint. This filter merely sets the decoded data in context, it does not validate
 * the relation between auth_time and max_age. It is left for a later filter.
 */
class IdTokenHintAuthenticationFilter(
    private val discovery: Discovery,
    private val serviceJwks: JsonWebKeySet
) : AuthenticationFilter() {

    private val logger = LoggerFactory.getLogger(IdTokenHintAuthenticationFilter::class.java)

    override fun shouldFilter(request: OidcAuthorizeRequest, rc: RoutingContext): Boolean {
        return super.shouldFilter(request, rc) && request.idTokenHint.isNotEmpty()
    }

    override suspend fun tryAuthenticate(request: OidcAuthorizeRequest, rc: RoutingContext) {
        val client = request.client.assertType<OidcClient>()
        val idTokenHint = request.idTokenHint

        verifyAndDecodeToken(idTokenHint, client).let { c ->
            // setup a request structure so auth_time can be validated
            AuthTimeValidator.validate(OidcAuthorizeRequest.Builder().also { b ->
                b.client = client
                b.maxAge = if (request.maxAge > 0) request.maxAge else
                    request.client.assertType<OidcClient>().defaultMaxAge
                b.prompts = request.prompts.toMutableSet()
                b.session = OidcSession(
                    authTime = c.authTime()
                )
            }.build())

            rc.setAuthentication(
                Authentication(
                    subject = c.subject,
                    authTime = c.authTime(),
                    acrValues = c.acrValues()
                )
            )

            logger.info("Authentication set via resolved id_token_hint.")
        }
    }

    private fun verifyAndDecodeToken(token: String, client: OidcClient): JwtClaims {
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
                            serviceJwks,
                            client.idTokenSignedResponseAlgorithm
                        )
                    )
                    .setExpectedIssuer(discovery.issuer)
                    .build()
                    .processToClaims(token)
            }
        }
    }
}