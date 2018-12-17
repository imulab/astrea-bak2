package io.imulab.astrea.service.proxy.filters.error

import com.netflix.zuul.exception.ZuulException
import io.imulab.astrea.sdk.oauth.error.OAuthException
import org.springframework.cloud.netflix.zuul.filters.post.SendErrorFilter
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants
import org.springframework.stereotype.Component

@Component
class RenderErrorFilter : SendErrorFilter() {

    // execute before the default send error filter
    override fun filterOrder(): Int = FilterConstants.SEND_ERROR_FILTER_ORDER - 1

    override fun findZuulException(throwable: Throwable?): ExceptionHolder {
        when (throwable) {
            is ZuulException -> {
                if (throwable.cause is OAuthException)
                    return OAuthExceptionHolder(throwable.cause as OAuthException)
                return super.findZuulException(throwable)
            }
            else -> return super.findZuulException(throwable)
        }
    }

    class OAuthExceptionHolder(private val oauthException: OAuthException) : ExceptionHolder {

        override fun getThrowable(): Throwable = oauthException

        override fun getErrorCause(): String = oauthException.message ?: oauthException.error

        override fun getStatusCode(): Int = oauthException.status
    }
}