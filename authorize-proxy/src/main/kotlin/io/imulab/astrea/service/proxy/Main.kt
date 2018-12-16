package io.imulab.astrea.service.proxy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.session.data.redis.RedisFlushMode
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession

const val XNonce = "x_nonce"
const val LoginToken = "login_token"
const val ConsentToken = "consent_token"

val reservedParams: List<String> = listOf(LoginToken, ConsentToken, XNonce)

@SpringBootApplication
@EnableZuulProxy
@EnableRedisHttpSession(redisFlushMode = RedisFlushMode.IMMEDIATE)
class AuthorizeEndpointProxy

fun main(args: Array<String>) {
    runApplication<AuthorizeEndpointProxy>(*args)
}