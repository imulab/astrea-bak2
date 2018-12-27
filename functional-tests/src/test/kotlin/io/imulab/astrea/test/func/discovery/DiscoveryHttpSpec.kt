package io.imulab.astrea.test.func.discovery

import com.typesafe.config.ConfigFactory
import io.imulab.astrea.test.func.ContainerFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import org.assertj.core.api.Assertions
import org.slf4j.LoggerFactory
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import kotlin.concurrent.thread

object DiscoveryHttpSpec : Spek({

    val logger = LoggerFactory.getLogger(DiscoveryHttpSpec::class.java)
    val config = ConfigFactory.load()
    val client = OkHttpClient()

    describe("Discovery HTTP API") {
        val discoveryService = ContainerFactory.createDiscovery(config, logger)

        before {
            Runtime.getRuntime().addShutdownHook(thread(start = false) {
                discoveryService.stop()
            })
        }

        it("should be running") {
            Assertions.assertThat(discoveryService.isRunning()).isTrue()
        }

        describe("getting discovery configuration") {
            val request = Request.Builder()
                .url("http://localhost:${discoveryService.getMappedPort(config.getInt("discovery.httpPort"))}")
                .build()
            val response = client.newCall(request).execute()

            it("should return 200 code") {
                Assertions.assertThat(response.code()).isEqualTo(200)
            }

            it("should contain canonical fields") {
                response.body()?.string()
                    ?.let {
                        logger.info(it)
                        ObjectMapper().readTree(it)
                    }
                    ?.apply {
                        Assertions.assertThat(has("issuer")).isTrue()
                        Assertions.assertThat(has("authorization_endpoint")).isTrue()
                        Assertions.assertThat(has("token_endpoint")).isTrue()
                    }
            }
        }

        after {
            discoveryService.stop()
        }
    }
})