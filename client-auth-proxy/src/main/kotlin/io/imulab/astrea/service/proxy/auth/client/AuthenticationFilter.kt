package io.imulab.astrea.service.proxy.auth.client

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AuthenticationFilter : ZuulFilter() {

    private val logger = LoggerFactory.getLogger(AuthenticationFilter::class.java)

    override fun run(): Any {
        val requestContext = RequestContext.getCurrentContext()

        logger.info("I am here.")

        return Unit
    }

    override fun shouldFilter(): Boolean = true

    override fun filterType(): String = "pre"

    override fun filterOrder(): Int = 1
}