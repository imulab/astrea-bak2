package io.imulab.astrea.service

import com.typesafe.config.ConfigFactory
import io.imulab.astrea.sdk.oauth.error.InvalidGrant
import io.vertx.core.Vertx
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.kodein.di.generic.instance
import org.spekframework.spek2.Spek
import java.util.concurrent.TimeUnit

object ExchangeIntegrationTest : Spek({

    given("Code exchange service") {
        val service by IntegrationTest(Vertx.vertx(), ConfigFactory.parseString(config))
            .bootstrap()
            .instance<AuthorizeCodeFlowService>()
        val server = IntegrationTest.startInProcessService(service)
        val stub = IntegrationTest.getInProcessServiceStub()

        `when`("refresh code is used to exchange for tokens") {
            val authorizeCode = stub.authorize(Narrative.authorizeRequest).let {
                assertThat(it.success).isTrue()
                return@let it.data.code
            }
            val response = stub.exchange(
                Narrative.tokenRequest.toBuilder()
                    .setCode(authorizeCode)
                    .build()
            )

            then("exchange should be successful") {
                assertThat(response.success).isTrue()
            }

            then("access token should have been issued") {
                assertThat(response.data.accessToken).isNotEmpty()
                assertThat(response.data.tokenType).isEqualTo("bearer")
                assertThat(response.data.expiresIn).isGreaterThan(0)
            }

            then("refresh token should not have been issued (not granted)") {
                assertThat(response.data.refreshToken).isEmpty()
            }

            then("id token should have been issued") {
                assertThat(response.data.idToken).isNotEmpty()
            }
        }

        `when`("bad code is used to exchange for tokens") {
            val response = stub.exchange(
                Narrative.tokenRequest.toBuilder()
                    .setCode("bad.authorize.code")
                    .build()
            )

            then("exchange should fail") {
                assertThat(response.success).isFalse()
            }

            then("error information should have been set") {
                assertThat(response.failure.error).isEqualTo(InvalidGrant.code)
            }
        }

        `when`("code is used twice") {
            val authorizeCode = stub.authorize(Narrative.authorizeRequest).let {
                assertThat(it.success).isTrue()
                return@let it.data.code
            }
            val request = Narrative.tokenRequest.toBuilder().setCode(authorizeCode).build()
            stub.exchange(request)
            val response = stub.exchange(request)

            then("exchange should fail") {
                assertThat(response.success).isFalse()
            }

            then("error information should have been set") {
                assertThat(response.failure.error).isEqualTo(InvalidGrant.code)
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