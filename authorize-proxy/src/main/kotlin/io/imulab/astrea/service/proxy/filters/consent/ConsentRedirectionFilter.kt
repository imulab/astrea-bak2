package io.imulab.astrea.service.proxy.filters.consent

import com.netflix.zuul.context.RequestContext
import io.imulab.astrea.sdk.oauth.reserved.Param
import io.imulab.astrea.service.proxy.XNonce
import io.imulab.astrea.service.proxy.XNonceStrategy
import okhttp3.HttpUrl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ConsentRedirectionFilter : ConsentFilter() {

    @Value("\${consent.url}")
    var consentServiceUrl: String = ""

    @Autowired
    lateinit var xNonceStrategy: XNonceStrategy

    override fun shouldFilter(): Boolean {
        val context = RequestContext.getCurrentContext()
        return super.shouldFilter() && context.requestQueryParams[Param.scope]?.get(0)?.isNotEmpty() ?: false
    }

    override fun run(): Any {
        val context = RequestContext.getCurrentContext()

        context.run {
            setSendZuulResponse(false)
            requestQueryParams[XNonce] = listOf(xNonceStrategy.encode())
            response.sendRedirect(
                HttpUrl.parse(consentServiceUrl)!!
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

    override fun filterOrder(): Int = BaseOrder + 20
}