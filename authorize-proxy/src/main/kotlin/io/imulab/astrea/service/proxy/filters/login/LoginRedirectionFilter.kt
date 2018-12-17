package io.imulab.astrea.service.proxy.filters.login

import com.netflix.zuul.context.RequestContext
import io.imulab.astrea.sdk.oauth.error.AccessDenied
import io.imulab.astrea.service.proxy.RedirectionSignal
import io.imulab.astrea.service.proxy.XNonce
import io.imulab.astrea.service.proxy.XNonceStrategy
import io.imulab.astrea.service.proxy.filters.LockParamsFilter
import okhttp3.HttpUrl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

/**
 * A [LoginFilter] to be placed at the end of the login filter chain. This filter is the last resort
 * of login processing as all other login filter failed to determine authentication status. Hence,
 * we redirect to the external login provider here.
 *
 * The request parameters are forwarded to the login provider and is expected to be sent back. This ensures that
 * the proxy does not have to endure database round trips to cache them, and thus gaining performance benefits.
 *
 * To ensure the login provider does not somehow rewrite the query parameters. A 'login_nonce' parameter is calculated
 * as a JWT containing base64 encoded SHA-256 hash of the query parameters and query parameter names, and is expected
 * to be send back along with the login response. Another filter, on receiving the login response, should validate the
 * integrity of the query parameters to ensure exactly the original request parameters plus the login response and
 * login_nonce is sent back.
 */
@Component
class LoginRedirectionFilter : LoginFilter() {

    @Value("\${login.url}") var loginServiceUrl: String = ""

    @Autowired lateinit var xNonceStrategy: XNonceStrategy

    override fun run(): Any {
        val context = RequestContext.getCurrentContext()

        if ((context[LockParamsFilter.Stage] as Int) > 0)
            throw AccessDenied.byServer("login provider was not able to establish user identity.")

        throw RedirectionSignal(
            status = HttpStatus.TEMPORARY_REDIRECT.value(),
            url = HttpUrl.parse(loginServiceUrl)!!
                .newBuilder()
                .also { b ->
                    context.requestQueryParams
                        .flatMap { e -> e.value.map { v -> e.key to v } }
                        .forEach { p -> b.addQueryParameter(p.first, p.second) }
                }
                .also { b ->
                    b.addQueryParameter(XNonce, xNonceStrategy.encode(context))
                }
                .build().toString()
        )
    }

    override fun filterOrder(): Int = BaseOrder + 30
}