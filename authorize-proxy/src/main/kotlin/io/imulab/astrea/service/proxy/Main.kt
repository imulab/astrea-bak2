package io.imulab.astrea.service.proxy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy

@SpringBootApplication
@EnableZuulProxy
class AuthorizeEndpointProxy

fun main(args: Array<String>) {
    runApplication<AuthorizeEndpointProxy>(*args)
}