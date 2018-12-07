package io.imulab.astrea.sdk.oidc.client

import com.nhaarman.mockitokotlin2.argThat
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.imulab.astrea.sdk.oauth.OAuthContext
import io.imulab.astrea.sdk.oauth.client.ClientLookup
import io.imulab.astrea.sdk.oauth.error.InvalidClient
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.request.OAuthRequestForm
import io.imulab.astrea.sdk.oauth.reserved.Param
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.`when`
import io.imulab.astrea.sdk.oidc.client.authn.ClientSecretJwtAuthenticator
import io.imulab.astrea.sdk.oidc.given
import io.imulab.astrea.sdk.oidc.request.OidcRequestForm
import io.imulab.astrea.sdk.oidc.reserved.AuthenticationMethod
import io.imulab.astrea.sdk.oidc.reserved.OidcParam
import io.imulab.astrea.sdk.oidc.reserved.jwtBearerClientAssertionType
import io.imulab.astrea.sdk.oidc.then
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.keys.AesKey
import org.spekframework.spek2.Spek

object ClientSecretJwtAuthenticatorSpec : Spek({

    given("A configured authenticator") {
        val given = Given()
        val authenticator = given.authenticator
        val correctSecret = given.secret.toByteArray()

        then("the authenticator support only client_secret_jwt") {
            assertThat(authenticator.supports(AuthenticationMethod.clientSecretJwt)).isTrue()
            assertThat(authenticator.supports(AuthenticationMethod.privateKeyJwt)).isFalse()
        }

        `when`("using token signed with correct client secret") {
            val result = runCatching {
                runBlocking { authenticator.authenticate(When(correctSecret).form) }
            }

            then("should have passed authentication") {
                assertThat(result.isSuccess).isTrue()
            }

            then("should have return the authenticated client") {
                assertThat(result.getOrNull()?.id).isEqualTo("foo")
            }
        }

        `when`("using token signed with correct client secret, but with incorrect assertion type") {
            val result = runCatching {
                runBlocking { authenticator.authenticate(When(correctSecret).form.also {
                    it.httpForm[OidcParam.clientAssertionType] = listOf("foo")
                }) }
            }

            then("should have been rejected") {
                assertThat(result.isFailure).isTrue()
                assertThat(result.exceptionOrNull()).isInstanceOf(OAuthException::class.java)
            }
        }

        `when`("using token signed with incorrect client secret") {
            val incorrectSecret = "7b1e1a62685a4d9eb12ec38b14f49d54".toByteArray()
            val result = runCatching {
                runBlocking { authenticator.authenticate(When(incorrectSecret).form) }
            }

            then("should have passed authentication") {
                assertThat(result.isFailure).isTrue()
                assertThat(result.exceptionOrNull()).isInstanceOf(OAuthException::class.java)
            }
        }
    }
}) {

    class Given {
        val secret: String = "af7132dd14df40888abc4af88a68cde9"

        private val client = mock<OidcClient> {
            onGeneric { id } doReturn "foo"
            onGeneric { secret } doReturn secret.toByteArray()
        }

        private val clientLookup = mock<ClientLookup> {
            onBlocking { find("foo") } doReturn client
            onBlocking { find(argThat { this != "foo" }) } doAnswer {
                throw InvalidClient.unknown()
            }
        }

        private val serverContext = mock<OAuthContext> {
            onGeneric { tokenEndpointUrl } doReturn "https://nix.com/oauth/token"
        }

        val authenticator = ClientSecretJwtAuthenticator(clientLookup, serverContext)
    }

    class When(private val secret: ByteArray) {

        private val token: String = JsonWebSignature().also { jws ->
            jws.key = AesKey(secret)
            jws.algorithmHeaderValue = JwtSigningAlgorithm.HS256.spec
            jws.payload = JwtClaims().also { c ->
                c.setGeneratedJwtId()
                c.setIssuedAtToNow()
                c.setExpirationTimeMinutesInTheFuture(10f)
                c.subject = "foo"
                c.issuer = "foo"
                c.setAudience("https://nix.com/oauth/token")
            }.toJson()
        }.compactSerialization

        val form: OAuthRequestForm = OidcRequestForm(
            mutableMapOf(
                Param.clientId to listOf("foo"),
                OidcParam.clientAssertion to listOf(token),
                OidcParam.clientAssertionType to listOf(jwtBearerClientAssertionType)
            )
        )
    }
}
