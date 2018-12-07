package io.imulab.astrea.sdk.oidc.client

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.imulab.astrea.sdk.oauth.client.ClientLookup
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.request.OAuthRequestForm
import io.imulab.astrea.sdk.oauth.reserved.ClientType
import io.imulab.astrea.sdk.oauth.reserved.GrantType
import io.imulab.astrea.sdk.oauth.reserved.Param
import io.imulab.astrea.sdk.oidc.`when`
import io.imulab.astrea.sdk.oidc.client.authn.NoneAuthenticator
import io.imulab.astrea.sdk.oidc.given
import io.imulab.astrea.sdk.oidc.request.OidcRequestForm
import io.imulab.astrea.sdk.oidc.reserved.AuthenticationMethod
import io.imulab.astrea.sdk.oidc.then
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek

object NoneAuthenticatorSpec : Spek({

    given("A configured authenticator") {
        val given = Given()
        val authenticator = given.authenticator

        then("supports only none method") {
            assertThat(authenticator.supports(AuthenticationMethod.none)).isTrue()
            assertThat(authenticator.supports(AuthenticationMethod.privateKeyJwt)).isFalse()
        }

        `when`("A public client authenticates") {
            val form = When("foo").form
            val result = runCatching {
                runBlocking { authenticator.authenticate(form) }
            }

            then("should pass authentication") {
                assertThat(result.isSuccess).isTrue()
                assertThat(result.getOrNull()?.id).isEqualTo("foo")
            }
        }

        `when`("A implicit flow only client authenticates") {
            val form = When("bar").form
            val result = runCatching {
                runBlocking { authenticator.authenticate(form) }
            }

            then("should pass authentication") {
                assertThat(result.isSuccess).isTrue()
                assertThat(result.getOrNull()?.id).isEqualTo("bar")
            }
        }

        `when`("A confidential client with multiple grant types authenticates") {
            val form = When("zoo").form
            val result = runCatching {
                runBlocking { authenticator.authenticate(form) }
            }

            then("should be rejected") {
                assertThat(result.isFailure).isTrue()
                assertThat(result.exceptionOrNull()).isInstanceOf(OAuthException::class.java)
            }
        }

    }
}) {

    class Given {
        private val publicClient = mock<OidcClient> {
            onGeneric { id } doReturn "foo"
            onGeneric { type } doReturn ClientType.public
        }

        private val implicitOnlyClient = mock<OidcClient> {
            onGeneric { id } doReturn "bar"
            onGeneric { type } doReturn ClientType.confidential
            onGeneric { grantTypes } doReturn setOf(GrantType.implicit)
        }

        private val zooClient = mock<OidcClient> {
            onGeneric { id } doReturn "zoo"
            onGeneric { type } doReturn ClientType.confidential
            onGeneric { grantTypes } doReturn setOf(GrantType.implicit, GrantType.authorizationCode)
        }

        private val clientLookup = mock<ClientLookup> {
            onBlocking { find("foo") } doReturn publicClient
            onBlocking { find("bar") } doReturn implicitOnlyClient
            onBlocking { find("zoo") } doReturn zooClient
        }

        val authenticator = NoneAuthenticator(clientLookup = clientLookup)
    }

    class When(clientId: String) {
        val form: OAuthRequestForm = OidcRequestForm(
            mutableMapOf(
                Param.clientId to listOf(clientId)
            )
        )
    }
}