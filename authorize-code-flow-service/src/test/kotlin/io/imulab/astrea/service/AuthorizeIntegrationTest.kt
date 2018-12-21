package io.imulab.astrea.service

import com.typesafe.config.ConfigFactory
import io.imulab.astrea.sdk.flow.CodeRequest
import io.vertx.core.Vertx
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.kodein.di.generic.instance
import org.spekframework.spek2.Spek
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

object AuthorizeIntegrationTest : Spek({

    given("Authorize code flow service") {
        val service by IntegrationTest(Vertx.vertx(), ConfigFactory.parseString(config))
            .bootstrap()
            .instance<AuthorizeCodeFlowService>()
        val server = IntegrationTest.startInProcessService(service)
        val stub = IntegrationTest.getInProcessServiceStub()

        `when`("requested to create an authorization code") {
            val response = stub.authorize(CodeRequest.newBuilder().apply {
                id = "472ffb2b-f7cd-4f14-b99f-2bb48f98b121"
                requestTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                addAllScopes(listOf("foo", "openid"))
                addResponseTypes("code")
                redirectUri = "https://test.com/callback"
                state = "12345678"
                client = CodeRequest.Client.newBuilder()
                    .setId("c1976ab7-ad39-4576-8881-6a5a3ef87e3a")
                    .addRedirectUris("https://test.com/callback")
                    .addAllScopes(listOf("foo", "bar", "openid"))
                    .addResponseTypes("code")
                    .build()
                session = CodeRequest.Session.newBuilder()
                    .setSubject("foo@bar.com")
                    .addAllGrantedScopes(listOf("foo", "openid"))
                    .setAuthenticationTime(LocalDateTime.now().minusMinutes(1).toEpochSecond(ZoneOffset.UTC))
                    .setNonce("87654321")
                    .build()
            }.build())

            then("request should be successful") {
                assertThat(response.success).isTrue()
            }

            then("an authorize code has been generated") {
                assertThat(response.data.code).isNotBlank()
            }

            then("scopes have been granted") {
                assertThat(response.data.scopesList).contains("foo", "openid")
            }
        }

        after {
            runBlocking {
                server.shutdown()
                server.awaitTermination(5, TimeUnit.SECONDS)
            }
        }
    }
})