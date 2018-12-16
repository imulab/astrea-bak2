package io.imulab.astrea.service.proxy.filters

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import io.imulab.astrea.service.proxy.filters.login.LoginFilter
import io.imulab.astrea.service.proxy.filters.login.LoginVerificationFilter.Companion.LoginTokenParam
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants

class SetAuthorizationFilter : ZuulFilter() {

    companion object {
        const val LoginHeader = "X-ASTREA-LOGIN"
        const val ConsentHeader = "X-ASTREA-CONSENT"
    }

    override fun run(): Any {
        setLoginClaimsToHeader()

        return Unit
    }

    private fun setLoginClaimsToHeader() {
        val context = RequestContext.getCurrentContext()

        check(context.getBoolean(LoginFilter.LoginApproved, false))
        check(context.requestQueryParams.containsKey(LoginTokenParam))

        val loginToken = context.requestQueryParams[LoginTokenParam]!![0]!!
        val claims = JwtConsumerBuilder()
            .also { b ->
                // skip all validations since it has been verified by a previous filter.
                b.setDisableRequireSignature()
                b.setSkipSignatureVerification()
                b.setSkipAllValidators()
            }
            .build()
            .processToClaims(loginToken)

        context.addZuulRequestHeader(LoginHeader, claims.toJson())
    }

    override fun shouldFilter(): Boolean = true

    override fun filterType(): String = FilterConstants.PRE_TYPE

    override fun filterOrder(): Int = 299
}