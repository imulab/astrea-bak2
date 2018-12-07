package io.imulab.astrea.sdk.oidc.client

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.imulab.astrea.sdk.oauth.client.ClientLookup
import io.imulab.astrea.sdk.oauth.request.OAuthRequestForm
import io.imulab.astrea.sdk.oauth.reserved.Param
import io.imulab.astrea.sdk.oidc.`when`
import io.imulab.astrea.sdk.oidc.client.authn.ClientSecretJwtAuthenticator
import io.imulab.astrea.sdk.oidc.client.authn.OidcClientAuthenticators
import io.imulab.astrea.sdk.oidc.client.authn.PrivateKeyJwtAuthenticator
import io.imulab.astrea.sdk.oidc.given
import io.imulab.astrea.sdk.oidc.request.OidcRequestForm
import io.imulab.astrea.sdk.oidc.reserved.AuthenticationMethod
import io.imulab.astrea.sdk.oidc.then
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek

object OidcClientAuthenticatorsSpec : Spek({

    given("A configured chain of authenticators") {
        val given = Given()
        val authenticator = given.authenticator

        `when`("Requested by a client with registered authentication method") {
            val result = runCatching {
                runBlocking { authenticator.authenticate(When("foo").form) }
            }

            then("The correct authenticator should have been invoked") {
                // Authenticator throws exception is the intended behaviour
                assertThat(
                    result.exceptionOrNull()
                        ?.stackTrace
                        ?.filter { it.className == PrivateKeyJwtAuthenticator::class.java.name }
                ).isNotNull
            }
        }
    }
}) {

    /**
     * Because we are mocking the request input. Any authenticator that actually starts to process
     * a request will throw NPE. And we can use the stacktrace to find out who was invoked.
     */
    class Given {
        private val clientSecretJwtAuthenticator = ClientSecretJwtAuthenticator(mock(), mock())
        private val privateKeyJwtAuthenticator = PrivateKeyJwtAuthenticator(mock(), mock(), mock())

        private val fooClient = mock<OidcClient> {
            onGeneric { id } doReturn "foo"
            onGeneric { tokenEndpointAuthenticationMethod } doReturn AuthenticationMethod.privateKeyJwt
        }

        private val clientLookup = mock<ClientLookup> {
            onBlocking { find("foo") } doReturn fooClient
        }

        val authenticator = OidcClientAuthenticators(
            listOf(clientSecretJwtAuthenticator, privateKeyJwtAuthenticator),
            clientLookup
        )
    }

    class When(clientId: String) {
        val form: OAuthRequestForm = OidcRequestForm(
            mutableMapOf(
                Param.clientId to listOf(clientId)
            )
        )
    }
}