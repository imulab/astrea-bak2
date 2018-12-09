package io.imulab.astrea.service.authorization.authz.consent

import io.imulab.astrea.sdk.oauth.reserved.Param
import io.imulab.astrea.sdk.oauth.response.OAuthResponse
import io.imulab.astrea.sdk.oidc.reserved.ConsentTokenParam

/**
 * Special exception to signal an HTTP redirection to the consent endpoint.
 */
class ConsentRedirectionSignal(
    val consentEndpoint: String,
    private val consentToken: String,
    private val authorizeRequestId: String,
    private val callbackUri: String,
    private val nonce: String
) : RuntimeException(), OAuthResponse {

    override val status: Int
        get() = 302
    override val headers: Map<String, String>
        get() = mapOf("Location" to consentEndpoint)
    override val data: Map<String, String>
        get() = mapOf(
            Param.Internal.consentToken to consentToken,
            ConsentTokenParam.authorizeRequestId to authorizeRequestId,
            ConsentTokenParam.nonce to nonce,
            ConsentTokenParam.redirectUri to callbackUri
        )
}