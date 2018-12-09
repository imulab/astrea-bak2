package io.imulab.astrea.service.authorization.authz.authn

import io.imulab.astrea.sdk.oauth.assertType
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.client.OidcClient
import io.imulab.astrea.sdk.oidc.discovery.OidcContext
import io.imulab.astrea.sdk.oidc.jwk.JwtVerificationKeyResolver
import io.imulab.astrea.sdk.oidc.jwk.authTime
import io.imulab.astrea.sdk.oidc.request.OidcAuthorizeRequest
import io.imulab.astrea.sdk.oidc.request.OidcRequestForm
import io.imulab.astrea.sdk.oidc.request.OidcSession
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import java.time.LocalDateTime

/**
 * Implementation of [AuthenticationHandler] to use an `id_token_hint` parameter as the truth source.
 *
 * The token hint must have been issued and signed by the server itself. There must exist a valid `auth_time` claim.
 * If max age is specified (either from request directly or from [OidcClient.defaultMaxAge]), `auth_time` must not
 * happen longer ago than the seconds specified by the max age.
 *
 * If all above conditions are met, the authentication is established. If any of the conditions are not met, the handler
 * simply returns (instead of raise error) to give other downstream authenticators a chance.
 *
 * Note, according to the specification, `id_token_hint` can be optionally re-encrypted by the client too. However,
 * encryption feature is currently not supported on `id_token_hint`. It will be supported in the future.
 */
class IdTokenHintAuthenticationHandler(
    private val oidcContext: OidcContext
) : AuthenticationHandler {

    override suspend fun attemptAuthenticate(form: OidcRequestForm, request: OidcAuthorizeRequest, rawCall: Any) {
        if (form.idTokenHint.isEmpty())
            return

        val tokenClaims = try {
            verifyTokenSignature(form.idTokenHint, request.client.assertType())
        } catch (e: Exception) {
            return
        }

        val authTimeClaim = tokenClaims.authTime() ?: return
        val maxAge = if (request.maxAge > 0) request.maxAge else
            request.client.assertType<OidcClient>().defaultMaxAge
        if (maxAge > 0 && authTimeClaim.plusSeconds(maxAge).isBefore(LocalDateTime.now()))
            return

        with(request.session.assertType<OidcSession>()) {
            subject = tokenClaims.subject
            authTime = authTimeClaim
        }
    }

    // use server's public key to verify it
    private fun verifyTokenSignature(token: String, client: OidcClient): JwtClaims {
        return when (client.idTokenSignedResponseAlgorithm) {
            JwtSigningAlgorithm.None -> {
                JwtConsumerBuilder()
                    .setRequireJwtId()
                    .setJwsAlgorithmConstraints(client.idTokenSignedResponseAlgorithm.whitelisted())
                    .setSkipVerificationKeyResolutionOnNone()
                    .setDisableRequireSignature()
                    .setExpectedIssuer(oidcContext.issuerUrl)
                    .build()
                    .processToClaims(token)
            }
            else -> {
                JwtConsumerBuilder()
                    .setRequireJwtId()
                    .setJwsAlgorithmConstraints(client.idTokenSignedResponseAlgorithm.whitelisted())
                    .setVerificationKeyResolver(
                        JwtVerificationKeyResolver(
                            oidcContext.masterJsonWebKeySet,
                            client.idTokenSignedResponseAlgorithm
                        )
                    )
                    .setExpectedIssuer(oidcContext.issuerUrl)
                    .build()
                    .processToClaims(token)
            }
        }
    }
}