package io.imulab.astrea.service.proxy

import com.netflix.zuul.ZuulFilter
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants

class ConsentFilter : ZuulFilter() {

    override fun run(): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun shouldFilter(): Boolean = true

    override fun filterType(): String = FilterConstants.PRE_TYPE

    override fun filterOrder(): Int = 2
}