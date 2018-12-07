package io.imulab.astrea.sdk.oidc.client

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.imulab.astrea.sdk.oauth.OAuthContext
import io.imulab.astrea.sdk.oauth.client.ClientLookup
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.request.OAuthRequestForm
import io.imulab.astrea.sdk.oauth.reserved.Param
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oauth.token.resolvePrivateKey
import io.imulab.astrea.sdk.oidc.`when`
import io.imulab.astrea.sdk.oidc.client.authn.PrivateKeyJwtAuthenticator
import io.imulab.astrea.sdk.oidc.given
import io.imulab.astrea.sdk.oidc.jwk.JsonWebKeySetStrategy
import io.imulab.astrea.sdk.oidc.request.OidcRequestForm
import io.imulab.astrea.sdk.oidc.reserved.AuthenticationMethod
import io.imulab.astrea.sdk.oidc.reserved.OidcParam
import io.imulab.astrea.sdk.oidc.reserved.jwtBearerClientAssertionType
import io.imulab.astrea.sdk.oidc.then
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.jose4j.jwk.JsonWebKey
import org.jose4j.jwk.JsonWebKeySet
import org.jose4j.jwk.RsaJwkGenerator
import org.jose4j.jwk.Use
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.keys.AesKey
import org.spekframework.spek2.Spek

object PrivateKeyJwtAuthenticatorSpec : Spek({

    given("A configured authenticator") {
        val given = Given()
        val authenticator = given.authenticator

        then("should support only private_key_jwt") {
            assertThat(authenticator.supports(AuthenticationMethod.privateKeyJwt)).isTrue()
            assertThat(authenticator.supports(AuthenticationMethod.clientSecretJwt)).isFalse()
        }

        `when`("authenticated with token signed by correct key") {
            val result = runCatching {
                runBlocking { authenticator.authenticate(When(given.key).form) }
            }

            then("should have passed authentication") {
                assertThat(result.isSuccess).isTrue()
            }

            then("should have return the authenticated client") {
                assertThat(result.getOrNull()?.id).isEqualTo("foo")
            }
        }

        `when`("authenticated with token signed by correct key, but without kid header hint") {
            val result = runCatching {
                runBlocking { authenticator.authenticate(When2(given.key).form) }
            }

            then("should have passed authentication") {
                assertThat(result.isSuccess).isTrue()
            }

            then("should have return the authenticated client") {
                assertThat(result.getOrNull()?.id).isEqualTo("foo")
            }
        }

        `when`("authenticated with token signed by correct key, but with incorrect assertion type") {
            val result = runCatching {
                runBlocking { authenticator.authenticate(When(given.key).form.also {
                    it.httpForm[OidcParam.clientAssertionType] = listOf("foo")
                }) }
            }

            then("should have been rejected") {
                assertThat(result.isFailure).isTrue()
                assertThat(result.exceptionOrNull()).isInstanceOf(OAuthException::class.java)
            }
        }

        `when`("authenticated with token signed by incorrect key") {
            val incorrectKey = RsaJwkGenerator.generateJwk(2048).also { k ->
                k.use = Use.SIGNATURE
                k.keyId = "27ca1312-5932-4146-b41d-a055929d728d"
                k.algorithm = JwtSigningAlgorithm.RS256.algorithmIdentifier
            }
            val result = runCatching {
                runBlocking { authenticator.authenticate(When(incorrectKey).form) }
            }

            then("should have been rejected") {
                assertThat(result.isFailure).isTrue()
                assertThat(result.exceptionOrNull()).isInstanceOf(OAuthException::class.java)
            }
        }

        `when`("authenticated with token signed with client secret") {
            val result = runCatching {
                runBlocking { authenticator.authenticate(When3(given.secret.toByteArray()).form) }
            }

            then("should have passed authentication") {
                assertThat(result.isSuccess).isTrue()
            }

            then("should have return the authenticated client") {
                assertThat(result.getOrNull()?.id).isEqualTo("foo")
            }
        }

        `when`("authenticated with token signed with incorrect client secret") {
            val incorrectSecret = "AED09DD1844B4D19ABAC73A82F4A96F1".toByteArray()
            val result = runCatching {
                runBlocking { authenticator.authenticate(When3(incorrectSecret).form) }
            }

            then("should have been rejected") {
                assertThat(result.isFailure).isTrue()
                assertThat(result.exceptionOrNull()).isInstanceOf(OAuthException::class.java)
            }
        }
    }
}) {

    class Given {
        val key: JsonWebKey = RsaJwkGenerator.generateJwk(2048).also { k ->
            k.use = Use.SIGNATURE
            k.keyId = "4f043cea-f8f3-4367-a1e0-76dd402fe4f4"
            k.algorithm = JwtSigningAlgorithm.RS256.algorithmIdentifier
        }

        val secret = "7b56604a383f405583736ca5539c48d9"

        private val clientJwksStrategy = mock<JsonWebKeySetStrategy> {
            onBlocking { resolveKeySet(any()) } doReturn JsonWebKeySet().also { s -> s.addJsonWebKey(key) }
        }

        private val fooClient = mock<OidcClient> {
            onGeneric { id } doReturn "foo"
            onGeneric { secret } doReturn secret.toByteArray()
        }

        private val clientLookup = mock<ClientLookup> {
            onBlocking { find("foo") } doReturn fooClient
        }

        private val serverContext = mock<OAuthContext> {
            onGeneric { tokenEndpointUrl } doReturn "https://nix.com/oauth/token"
        }

        val authenticator = PrivateKeyJwtAuthenticator(
            clientLookup = clientLookup,
            clientJwksStrategy = clientJwksStrategy,
            oauthContext = serverContext
        )
    }

    /**
     * Provides a form with authentication set
     */
    class When(key: JsonWebKey) {

        private val token = JsonWebSignature().also { jws ->
            jws.key = key.resolvePrivateKey()
            jws.algorithmHeaderValue = JwtSigningAlgorithm.RS256.spec
            jws.keyIdHeaderValue = key.keyId
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

    /**
     * Provides a form with authentication set, but kid header of the token is not set.
     */
    class When2(key: JsonWebKey) {

        private val token = JsonWebSignature().also { jws ->
            jws.key = key.resolvePrivateKey()
            jws.algorithmHeaderValue = JwtSigningAlgorithm.RS256.spec
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

    /**
     * Provides a form with authentication set, with token signed by a secret using HS256.
     */
    class When3(secret: ByteArray) {
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