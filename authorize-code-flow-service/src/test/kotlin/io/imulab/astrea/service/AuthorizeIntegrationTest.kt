package io.imulab.astrea.service

import com.typesafe.config.ConfigFactory
import io.imulab.astrea.sdk.oauth.error.InvalidRequest
import io.imulab.astrea.sdk.oauth.error.InvalidScope
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.imulab.astrea.sdk.oauth.reserved.ResponseType
import io.vertx.core.Vertx
import org.assertj.core.api.Assertions.assertThat
import org.kodein.di.generic.instance
import org.spekframework.spek2.Spek

object AuthorizeIntegrationTest : Spek({

    given("Authorize code flow service") {
        val service by IntegrationTest(Vertx.vertx(), ConfigFactory.parseString(config))
            .bootstrap()
            .instance<AuthorizeCodeFlowService>()
        val serverContext = IntegrationTest.startInProcessService(service)
        val stub = serverContext.stub()

        `when`("requested to create an authorization code") {
            val response = stub.authorize(Narrative.authorizeRequest)

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

        `when`("request does not conform to authorization code flow") {
            val response = stub.authorize(
                Narrative.authorizeRequest.toBuilder()
                    .clearResponseTypes()
                    .addResponseTypes(ResponseType.token)
                    .build()
            )

            then("response should fail") {
                assertThat(response.success).isFalse()
            }

            then("error information should have been set") {
                assertThat(response.failure.error).isEqualTo(ServerError.code)
                assertThat(response.failure.description).isNotEmpty()
                assertThat(response.failure.status).isGreaterThanOrEqualTo(400)
            }
        }

        `when`("request provides unregistered redirect uri") {
            val response = stub.authorize(
                Narrative.authorizeRequest.toBuilder()
                    .setRedirectUri("https://malicious.com/callback")
                    .build()
            )

            then("response should fail") {
                assertThat(response.success).isFalse()
            }

            then("error information should have been set") {
                assertThat(response.failure.error).isEqualTo(InvalidRequest.code)
                assertThat(response.failure.description).isNotEmpty()
                assertThat(response.failure.status).isGreaterThanOrEqualTo(400)
            }
        }

        `when`("request has no scopes granted") {
            val response = stub.authorize(
                Narrative.authorizeRequest
                    .toBuilder()
                    .setSession(
                        Narrative.authorizeRequest.session.toBuilder()
                            .clearGrantedScopes()
                            .build()
                    )
                    .build()
            )

            then("response should fail") {
                assertThat(response.success).isFalse()
            }

            then("error information should have been set") {
                assertThat(response.failure.error).isEqualTo(InvalidScope.code)
                assertThat(response.failure.description).isNotEmpty()
                assertThat(response.failure.status).isGreaterThanOrEqualTo(400)
            }
        }

        `when`("request has unrequested scopes granted") {
            val response = stub.authorize(
                Narrative.authorizeRequest
                    .toBuilder()
                    .setSession(
                        Narrative.authorizeRequest.session.toBuilder()
                            .addGrantedScopes("bar")
                            .build()
                    )
                    .build()
            )

            then("response should fail") {
                assertThat(response.success).isFalse()
            }

            then("error information should have been set") {
                assertThat(response.failure.error).isEqualTo(InvalidScope.code)
                assertThat(response.failure.description).isNotEmpty()
                assertThat(response.failure.status).isGreaterThanOrEqualTo(400)
            }
        }

        after {
            serverContext.shutdownHook()
        }
    }
})