package io.imulab.astrea.service.proxy.config

import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import io.grpc.ManagedChannelBuilder
import io.imulab.astrea.sdk.discovery.GrpcDiscoveryService
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.vavr.control.Try
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class DiscoveryServiceConfiguration(private val serviceProperties: DiscoveryServiceProperties) {

    private val logger = LoggerFactory.getLogger(DiscoveryServiceConfiguration::class.java)

    private fun channel() = ManagedChannelBuilder.forAddress(
        serviceProperties.host,
        serviceProperties.port
    ).enableRetry().maxRetryAttempts(10).usePlaintext().build()

    @Bean
    fun discovery(): io.imulab.astrea.sdk.oidc.discovery.Discovery {
        val retry = Retry.of("discovery", RetryConfig.Builder()
            .maxAttempts(5)
            .waitDuration(Duration.ofSeconds(10))
            .retryExceptions(Exception::class.java)
            .build()
        )

        val discovery = Retry.decorateSupplier(retry) {
            runBlocking {
                GrpcDiscoveryService(channel()).getDiscovery()
            }.also {
                logger.info("Acquired discovery configuration.")
            }
        }

        return Try.ofSupplier(discovery).getOrElse { throw ServerError.internal("Cannot obtain discovery.") }
    }
}

@Configuration
@ConfigurationProperties("discovery")
class DiscoveryServiceProperties {
    var host: String = ""
    var port: Int = 0
}