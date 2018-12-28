package io.imulab.astrea.test.func.discovery

import com.fasterxml.jackson.databind.ObjectMapper
import com.typesafe.config.ConfigFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import org.assertj.core.api.Assertions
import org.slf4j.LoggerFactory
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object DiscoveryHttpSpec : Spek({

    val logger = LoggerFactory.getLogger(DiscoveryHttpSpec::class.java)
    val config = ConfigFactory.load()
    val client = OkHttpClient()

    describe("Discovery HTTP API") {

        describe("getting health status") {
            val request = Request.Builder()
                .url(config.getString("discovery.url") + "/health")
                .build()
            val response = client.newCall(request).execute()

            it("should return 200 code") {
                Assertions.assertThat(response.code()).isEqualTo(200)
            }
        }

        describe("getting discovery configuration") {
            val request = Request.Builder()
                .url(config.getString("discovery.url"))
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
    }
})