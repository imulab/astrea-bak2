package io.imulab.astrea.service.proxy.filters.consent

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants

abstract class ConsentFilter : ZuulFilter() {

    companion object {
        const val BaseOrder = 100
        const val ConsentAcquired = "CONSENT_ACQUIRED"
    }

    override fun filterType(): String = FilterConstants.PRE_TYPE

    override fun shouldFilter(): Boolean {
        val context = RequestContext.getCurrentContext()
        return !context.getBoolean(ConsentAcquired, false)
    }

    protected fun setAcquired() {
        RequestContext.getCurrentContext().set(ConsentAcquired, true)
    }
}