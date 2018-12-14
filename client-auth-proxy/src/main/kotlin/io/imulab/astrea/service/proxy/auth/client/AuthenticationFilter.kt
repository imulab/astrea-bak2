package io.imulab.astrea.service.proxy.auth.client

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import io.imulab.astrea.sdk.oauth.reserved.Header
import io.imulab.astrea.sdk.oidc.client.authn.OidcClientAuthenticators
import io.imulab.astrea.sdk.oidc.request.OidcRequestForm
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component

@Component
class AuthenticationFilter(private val clientAuthenticators: OidcClientAuthenticators) : ZuulFilter() {

    private val logger = LoggerFactory.getLogger(AuthenticationFilter::class.java)

    override fun run(): Any {
        val request = RequestContext.getCurrentContext().request
        val params = HashMap<String, List<String>>().apply {
            putAll(request.parameterMap.map { it.key to it.value.toList() })
            put(Header.authorization, listOf(request.getHeader(HttpHeaders.AUTHORIZATION) ?: ""))
        }

        logger.info(params.entries.joinToString(",") { "${it.key}:${it.value[0]}" })

        val client = runBlocking { clientAuthenticators.authenticate(OidcRequestForm(params)) }

        RequestContext.getCurrentContext().addZuulRequestHeader("X-ASTREA-AUTHENTICATED-CLIENT", client.id)

        return Unit
    }

    override fun shouldFilter(): Boolean = true

    override fun filterType(): String = FilterConstants.PRE_TYPE

    override fun filterOrder(): Int = 1
}