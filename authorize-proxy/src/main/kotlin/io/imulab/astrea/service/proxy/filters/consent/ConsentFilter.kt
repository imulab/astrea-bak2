package io.imulab.astrea.service.proxy.filters.consent

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import org.jose4j.jwt.JwtClaims
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants

abstract class ConsentFilter : ZuulFilter() {

    companion object {
        const val BaseOrder = 100
        const val ConsentClaims = "CONSENT_CLAIMS"
    }

    override fun filterType(): String = FilterConstants.PRE_TYPE

    override fun shouldFilter(): Boolean {
        return !RequestContext.getCurrentContext().containsKey(ConsentClaims)
    }

    protected fun setConsentClaims(claims: JwtClaims) {
        RequestContext.getCurrentContext().set(ConsentClaims, claims)
    }

    protected fun getConsentClaims(): JwtClaims? {
        return RequestContext.getCurrentContext()[ConsentClaims] as? JwtClaims
    }
}