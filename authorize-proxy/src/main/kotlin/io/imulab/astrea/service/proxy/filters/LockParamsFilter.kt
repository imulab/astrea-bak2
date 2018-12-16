package io.imulab.astrea.service.proxy.filters

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import io.imulab.astrea.sdk.oauth.error.AccessDenied
import io.imulab.astrea.service.proxy.XNonce
import io.imulab.astrea.service.proxy.XNonceStrategy
import org.jose4j.lang.JoseException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants
import org.springframework.stereotype.Component

@Component
class LockParamsFilter: ZuulFilter() {

    private val logger = LoggerFactory.getLogger(LockParamsFilter::class.java)

    @Autowired
    lateinit var xNonceStrategy: XNonceStrategy

    companion object {
        const val ParameterHash = "ParameterHash"
    }

    override fun run(): Any {
        val context = RequestContext.getCurrentContext()

        val hash = xNonceStrategy.calculateHash()
        context.set(ParameterHash, hash)

        if (context.requestQueryParams.containsKey(XNonce))
            ensureNotTempered(hash)

        return Unit
    }

    private fun ensureNotTempered(hash: String) {
        val tempered = try {
            val claims = xNonceStrategy.decode(RequestContext.getCurrentContext().requestQueryParams[XNonce]!![0]!!)
            val originalHash = claims.getStringClaimValue(XNonceStrategy.RequestHashClaim)

            hash != originalHash
        } catch (e: JoseException) {
            logger.debug("Verification encountered error, request assumed to have been tempered.", e)
            true
        }

        if (tempered)
            throw AccessDenied.byServer("Hash mismatch. Request may have been tempered.")
    }

    override fun shouldFilter(): Boolean = true

    override fun filterType(): String = FilterConstants.PRE_TYPE

    override fun filterOrder(): Int = 0
}