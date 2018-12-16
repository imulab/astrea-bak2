package io.imulab.astrea.service.proxy.filters.login

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants

abstract class LoginFilter : ZuulFilter() {

    companion object {
        const val BaseOrder = 10
        const val LoginApproved = "LOGIN_APPROVED"
    }

    override fun filterType(): String = FilterConstants.PRE_TYPE

    override fun shouldFilter(): Boolean {
        val context = RequestContext.getCurrentContext()
        return !context.getBoolean(LoginApproved, false)
    }

    protected fun setApproved() {
        RequestContext.getCurrentContext().set(LoginApproved, true)
    }
}