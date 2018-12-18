package io.imulab.astrea.service.proxy.filters

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import io.imulab.astrea.service.proxy.ConsentToken
import io.imulab.astrea.service.proxy.LoginToken
import io.imulab.astrea.service.proxy.filters.consent.ConsentFilter.Companion.ConsentClaims
import io.imulab.astrea.service.proxy.filters.login.LoginFilter.Companion.LoginClaims
import org.jose4j.jwt.JwtClaims
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants
import org.springframework.stereotype.Component

@Component
class SetAuthorizationFilter : ZuulFilter() {

    companion object {
        const val LoginHeader = "X-ASTREA-LOGIN"
        const val ConsentHeader = "X-ASTREA-CONSENT"
    }

    override fun run(): Any {
        val context = RequestContext.getCurrentContext()

        context.addZuulRequestHeader(LoginHeader, (context[LoginClaims] as? JwtClaims ?: JwtClaims()).toJson())
        context.addZuulRequestHeader(ConsentHeader, (context[ConsentClaims] as? JwtClaims ?: JwtClaims()).toJson())

        context.requestQueryParams.remove(LoginToken)
        context.requestQueryParams.remove(ConsentToken)

        return Unit
    }

    override fun shouldFilter(): Boolean = true

    override fun filterType(): String = FilterConstants.PRE_TYPE

    override fun filterOrder(): Int = 299
}