package io.imulab.astrea.service.proxy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy

const val XNonce = "x_nonce"
const val LoginToken = "login_token"
const val ConsentToken = "consent_token"

val reservedParams: List<String> = listOf(LoginToken, ConsentToken, XNonce)

@SpringBootApplication
@EnableZuulProxy
class AuthorizeEndpointProxy

fun main(args: Array<String>) {
    runApplication<AuthorizeEndpointProxy>(*args)
}