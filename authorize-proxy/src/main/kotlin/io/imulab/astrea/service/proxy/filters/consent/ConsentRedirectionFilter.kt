package io.imulab.astrea.service.proxy.filters.consent

import com.netflix.zuul.context.RequestContext
import io.imulab.astrea.sdk.oauth.error.AccessDenied
import io.imulab.astrea.sdk.oauth.reserved.Param
import io.imulab.astrea.service.proxy.RedirectionSignal
import io.imulab.astrea.service.proxy.XNonce
import io.imulab.astrea.service.proxy.XNonceStrategy
import io.imulab.astrea.service.proxy.filters.LockParamsFilter
import okhttp3.HttpUrl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
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

        if ((context[LockParamsFilter.Stage] as Int) > 1)
            throw AccessDenied.byServer("consent provider was not able to acquire user consent.")

        throw RedirectionSignal(
            status = HttpStatus.TEMPORARY_REDIRECT.value(),
            url = HttpUrl.parse(consentServiceUrl)!!
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

    override fun filterOrder(): Int = BaseOrder + 20
}