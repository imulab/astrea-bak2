package io.imulab.astrea.service.authorization.authz.consent

import io.imulab.astrea.sdk.oauth.assertType
import io.imulab.astrea.sdk.oidc.request.OidcAuthorizeRequest
import io.imulab.astrea.sdk.oidc.request.OidcRequestForm
import io.imulab.astrea.sdk.oidc.request.OidcSession
import io.imulab.astrea.sdk.oidc.reserved.StandardClaim

/**
 * Development/Test only implementation of [ConsentHandler]. This handler aims at granting all of the requested scopes
 * and put some fake idTokenClaims into the session.
 *
 * Do NOT use this in production.
 */
class AutoGrantConsentHandler(
    private val fakeClaims: Map<String, Any> = mapOf(
        StandardClaim.email to "foo@bar.com",
        StandardClaim.givenName to "Foo",
        StandardClaim.familyName to "Bar",
        StandardClaim.nickname to "foobar",
        StandardClaim.website to "https://foobar.com"
    )
) : ConsentHandler {

    override suspend fun attemptAuthorize(form: OidcRequestForm, request: OidcAuthorizeRequest, rawCall: Any) {
        request.scopes.forEach { request.grantScope(it) }
        request.session.assertType<OidcSession>().idTokenClaims.putAll(fakeClaims)
    }
}