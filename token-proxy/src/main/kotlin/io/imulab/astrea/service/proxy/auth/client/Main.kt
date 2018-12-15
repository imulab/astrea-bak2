package io.imulab.astrea.service.proxy.auth.client

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy

@SpringBootApplication
@EnableZuulProxy
class ClientAuthenticationProxy

fun main(args: Array<String>) {
    runApplication<ClientAuthenticationProxy>(*args)
}
