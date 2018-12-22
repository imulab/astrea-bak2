package io.imulab.astrea.service.authn

import io.imulab.astrea.sdk.oauth.assertType
import io.imulab.astrea.sdk.oauth.error.AccessDenied
import io.imulab.astrea.sdk.oidc.request.OidcSession
import io.imulab.astrea.service.RedirectionSignal
import io.imulab.astrea.service.getOidcAuthorizeRequest
import io.imulab.astrea.service.lock.ParameterLocker
import io.imulab.astrea.service.lock.parameterLockParam
import io.vertx.ext.web.RoutingContext
import okhttp3.HttpUrl

class AuthenticationHandler(
    private val loginProviderUrl: String,
    private val filters: List<AuthenticationFilter>,
    private val locker: ParameterLocker
) {

    suspend fun authenticateOrRedirect(rc: RoutingContext) {
        val request = rc.getOidcAuthorizeRequest()!!

        /**
         * Let authentication filters work their magic to try
         * establish an authentication context.
         */
        filters.forEach { f ->
            f.authenticate(request, rc)
        }

        /**
         * Transfer established authentication context to request session,
         * and continue processing.
         */
        val auth = rc.getAuthentication()
        if (auth != null) {
            request.session.assertType<OidcSession>().apply {
                subject = auth.subject
                authTime = auth.authTime
                acrValues.addAll(auth.acrValues)
            }
            return
        }

        if (locker.hasVisitedAuthenticationBefore(rc)) {
            throw AccessDenied.byServer("Server was not able to establish user identity.")
        }

        // prepare for login redirection
        val url = HttpUrl.parse(loginProviderUrl)!!
            .newBuilder()
            .also { b ->
                rc.request().params().forEach { p -> b.addQueryParameter(p.key, p.value) }
                b.addQueryParameter(parameterLockParam, locker.createLockForAuthenticationStage(rc))
            }
            .build()
            .toString()

        throw RedirectionSignal(url)
    }
}