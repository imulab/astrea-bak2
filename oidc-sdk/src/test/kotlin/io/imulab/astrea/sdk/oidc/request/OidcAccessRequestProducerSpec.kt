package io.imulab.astrea.sdk.oidc.request

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.imulab.astrea.sdk.oauth.assertType
import io.imulab.astrea.sdk.oauth.client.authn.ClientAuthenticators
import io.imulab.astrea.sdk.oauth.request.OAuthAccessRequest
import io.imulab.astrea.sdk.oauth.reserved.GrantType
import io.imulab.astrea.sdk.oauth.reserved.Param
import io.imulab.astrea.sdk.oauth.validation.OAuthGrantTypeValidator
import io.imulab.astrea.sdk.oidc.`when`
import io.imulab.astrea.sdk.oidc.client.OidcClient
import io.imulab.astrea.sdk.oidc.given
import io.imulab.astrea.sdk.oidc.then
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek

object OidcAccessRequestProducerSpec : Spek({

    given("An access request form") {
        val producer = Given().producer


        `when`("Produced an access request") {
            val form = OidcRequestForm(mutableMapOf(
                Param.clientId to listOf("foo"),
                Param.code to listOf("some-code"),
                Param.grantType to listOf("client_credentials"),
                Param.scope to listOf("foo bar"),
                Param.redirectUri to listOf("app://link")
            ))
            val request = runBlocking { producer.produce(form) }

            then("Request is an access request") {
                assertThat(request).isInstanceOf(OAuthAccessRequest::class.java)
            }

            then("Request should have an oidc session") {
                assertThat(request.session).isInstanceOf(OidcSession::class.java)
            }

            then("Request should parsed all parameters") {
                request.assertType<OAuthAccessRequest>().run {
                    assertThat(client.id).isEqualTo("foo")
                    assertThat(code).isEqualTo("some-code")
                    assertThat(grantTypes).contains(GrantType.clientCredentials)
                    assertThat(scopes).contains("foo", "bar")
                    assertThat(redirectUri).isEqualTo("app://link")
                }
            }

        }
    }
}) {

    class Given {
        private val client = mock<OidcClient> {
            onGeneric { id } doReturn "foo"
        }

        private val authenticator = mock<ClientAuthenticators> {
            onBlocking { authenticate(any()) } doReturn client
        }

        val producer = OidcAccessRequestProducer(
            grantTypeValidator = OAuthGrantTypeValidator,
            clientAuthenticators = authenticator
        )
    }
}