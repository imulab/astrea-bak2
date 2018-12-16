package io.imulab.astrea.service.proxy.login

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants
import java.security.MessageDigest
import java.util.*

abstract class LoginFilter : ZuulFilter() {

    companion object {
        const val LoginApproved = "LOGIN_APPROVED"
        const val XNonceParam = "x_nonce"
    }

    override fun filterType(): String = FilterConstants.PRE_TYPE

    override fun shouldFilter(): Boolean {
        val context = RequestContext.getCurrentContext()
        return !context.getBoolean(LoginApproved, false)
    }

    protected fun hashRequestQueryParams(
        entryFilter: (Map.Entry<String, List<String>>) -> Boolean = { true }
    ): String {
        val joined = RequestContext.getCurrentContext()
            .requestQueryParams.toSortedMap()
            .entries
            .filter(entryFilter)
            .joinToString { it.key + ":" + it.value.joinToString() }

        return MessageDigest.getInstance("SHA-256")
            .digest(joined.toByteArray())
            .let { Base64.getUrlEncoder().withoutPadding().encodeToString(it) }
    }

    protected fun setApproved() {
        RequestContext.getCurrentContext().set(LoginApproved, true)
    }

    protected fun hasXNonce() = RequestContext.getCurrentContext().containsKey(XNonceParam)
}