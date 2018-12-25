package io.imulab.astrea.service.authn

import com.typesafe.config.Config
import io.imulab.astrea.sdk.oidc.request.OidcAuthorizeRequest
import io.vertx.ext.web.RoutingContext
import java.time.LocalDateTime

class AutoLoginAuthenticationFilter(
    private val config: Config,
    private val subject: String = "foo@bar.com"
) : AuthenticationFilter() {

    override fun shouldFilter(request: OidcAuthorizeRequest, rc: RoutingContext): Boolean {
        return super.shouldFilter(request, rc) && config.getBoolean("service.autoLogin")
    }

    override suspend fun tryAuthenticate(request: OidcAuthorizeRequest, rc: RoutingContext) {
        rc.setAuthentication(
            Authentication(
                subject = subject,
                authTime = LocalDateTime.now().minusSeconds(5),
                acrValues = listOf("0")
            )
        )
    }
}