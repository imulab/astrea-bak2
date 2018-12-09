package io.imulab.astrea.service.authorization.config

import io.imulab.astrea.sdk.oauth.client.pwd.BCryptPasswordEncoder
import io.imulab.astrea.sdk.oauth.reserved.ClientType
import io.imulab.astrea.sdk.oauth.reserved.GrantType
import io.imulab.astrea.sdk.oauth.reserved.ResponseType
import io.imulab.astrea.sdk.oauth.reserved.StandardScope
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.JweKeyManagementAlgorithm
import io.imulab.astrea.service.authorization.client.NixClient
import org.jose4j.jwk.RsaJsonWebKey
import org.jose4j.jwk.RsaJwkGenerator
import org.jose4j.jwk.Use
import org.jose4j.keys.AesKey
import org.springframework.context.annotation.Profile
import java.nio.charset.StandardCharsets

@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Profile("memory")
annotation class Memory

fun String.toAesKey(): AesKey = AesKey(toByteArray(StandardCharsets.UTF_8))

object Stock {

    val clientFoo = NixClient.Builder().also { b ->
        b.id = "foo"
        b.name = "Foo"
        b.type = ClientType.confidential
        b.secret = BCryptPasswordEncoder().encode("s3cret").toByteArray()
        b.responseTypes = mutableSetOf(
            ResponseType.code,
            ResponseType.token,
            io.imulab.astrea.sdk.oidc.reserved.ResponseType.idToken
        )
        b.grantTypes = mutableSetOf(
            GrantType.authorizationCode,
            GrantType.clientCredentials,
            GrantType.implicit,
            GrantType.refreshToken
        )
        b.redirectUris = mutableSetOf(
            "http://localhost:8888/callback"
        )
        b.scopes = mutableSetOf(
            "foo",
            "bar",
            StandardScope.offlineAccess,
            io.imulab.astrea.sdk.oidc.reserved.StandardScope.openid
        )
        b.requireAuthTime = true
    }.build()

    val signatureKey: RsaJsonWebKey = RsaJwkGenerator.generateJwk(2048).also { k ->
        k.keyId = "c7b318fa-4fc0-4beb-baef-91285c7ba628"
        k.use = Use.SIGNATURE
        k.algorithm = JwtSigningAlgorithm.RS256.algorithmIdentifier
    }

    val encryptionKey: RsaJsonWebKey = RsaJwkGenerator.generateJwk(2048).also { k ->
        k.keyId = "1eb56508-347b-4c8e-b040-5113aa18f26b"
        k.use = Use.ENCRYPTION
        k.algorithm = JweKeyManagementAlgorithm.RSA1_5.algorithmIdentifier
    }
}