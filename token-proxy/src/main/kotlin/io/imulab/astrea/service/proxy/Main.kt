package io.imulab.astrea.service.proxy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy

@SpringBootApplication
@EnableZuulProxy
class TokenEndpointProxy

fun main(args: Array<String>) {
    runApplication<TokenEndpointProxy>(*args)
}
