package io.imulab.astrea.test.func.discovery

import com.typesafe.config.ConfigFactory
import io.imulab.astrea.test.func.ContainerFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.slf4j.LoggerFactory
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper

@Testcontainers
class DiscoveryHttpTests {

    companion object {
        private val logger = LoggerFactory.getLogger(DiscoveryHttpTests::class.java)
        private val config = ConfigFactory.load()
        private val client = OkHttpClient()

        @Container
        private val discoveryService = ContainerFactory.createDiscovery(config, logger)
    }

    @Test
    fun `service should be running`() {
        assertThat(discoveryService.isRunning()).isTrue()
    }

    @Test
    fun `api returns discovery configuration`() {
        val request = Request.Builder()
            .url("http://localhost:${discoveryService.getMappedPort(config.getInt("discovery.httpPort"))}")
            .build()
        val response = client.newCall(request).execute()

        assertThat(response.code()).isEqualTo(200)

        response.body()?.string()
            ?.let {
                logger.info(it)
                ObjectMapper().readTree(it)
            }
            ?.apply {
                assertThat(has("issuer")).isTrue()
                assertThat(has("authorization_endpoint")).isTrue()
                assertThat(has("token_endpoint")).isTrue()
            }
    }
}