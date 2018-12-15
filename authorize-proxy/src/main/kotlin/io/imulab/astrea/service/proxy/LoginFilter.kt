package io.imulab.astrea.service.proxy

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants

class LoginFilter : ZuulFilter() {

    override fun run(): Any {
        val context = RequestContext.getCurrentContext()
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun shouldFilter(): Boolean = true

    override fun filterType(): String = FilterConstants.PRE_TYPE

    override fun filterOrder(): Int = 1
}