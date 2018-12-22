package io.imulab.astrea.service.authn

import io.imulab.astrea.sdk.oidc.request.OidcAuthorizeRequest
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

abstract class AuthenticationFilter {

    private val logger = LoggerFactory.getLogger(AuthenticationFilter::class.java)

    private val authKey = "authKey"

    open fun shouldFilter(request: OidcAuthorizeRequest, rc: RoutingContext): Boolean {
        return rc.getAuthentication() == null
    }

    suspend fun authenticate(request: OidcAuthorizeRequest, rc: RoutingContext) {
        if (shouldFilter(request, rc))
            try {
                tryAuthenticate(request, rc)
            } catch (t: Throwable) {
                if (shouldBeSuppressed(t)) {
                    logger.info("Suppressed authentication filter error <{}>. Assuming failed, moving on to next filter.", t)
                } else {
                    logger.error("Error during authentication.", t)
                    throw t
                }
            }
    }

    open fun shouldBeSuppressed(t: Throwable): Boolean = true

    abstract suspend fun tryAuthenticate(request: OidcAuthorizeRequest, rc: RoutingContext)

    protected fun RoutingContext.getAuthentication(): Authentication? = try {
        get<Authentication>(authKey)
    } catch (t: Throwable) {
        null
    }

    protected fun RoutingContext.setAuthentication(auth: Authentication) {
        put(authKey, auth)
    }

    protected fun RoutingContext.removeAuthentication() {
        remove<Any>(authKey)
    }
}

class Authentication(
    val subject: String,
    val authTime: LocalDateTime? = null,
    val acrValues: List<String> = emptyList()
)
