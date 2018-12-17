package io.imulab.astrea.service.proxy.filters.login

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import org.jose4j.jwt.JwtClaims
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants

abstract class LoginFilter : ZuulFilter() {

    companion object {
        const val BaseOrder = 10
        const val LoginClaims = "LOGIN_CLAIMS"
    }

    override fun filterType(): String = FilterConstants.PRE_TYPE

    override fun shouldFilter(): Boolean {
        val context = RequestContext.getCurrentContext()
        return !context.containsKey(LoginClaims)
    }

    protected fun setLoginClaims(claims: JwtClaims) {
        RequestContext.getCurrentContext().set(LoginClaims, claims)
    }

    protected fun getLoginClaims(): JwtClaims? {
        return RequestContext.getCurrentContext()[LoginClaims] as? JwtClaims
    }
}