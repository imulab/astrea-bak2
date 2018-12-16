package io.imulab.astrea.service.proxy

import com.netflix.zuul.context.RequestContext
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.jose4j.keys.AesKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.util.*

@Component
class XNonceStrategy {

    companion object {
        const val RequestHashClaim = "req_hash"
    }

    @Value("\${service.name}")
    var serviceName: String = ""

    @Value("\${service.signingKey}")
    var signingKey: String = ""

    fun calculateHash(): String {
        return RequestContext.getCurrentContext()
            .requestQueryParams
            .filterKeys { !reservedParams.contains(it) }
            .toSortedMap().entries
            .joinToString { it.key + ":" + it.value.joinToString() }
            .let { s -> MessageDigest.getInstance("SHA-256").digest(s.toByteArray()) }
            .let { b -> Base64.getEncoder().withoutPadding().encodeToString(b) }
    }

    fun encode(): String {
        return JsonWebSignature().also { jws ->
            jws.payload = JwtClaims().also { c ->
                c.setGeneratedJwtId()
                c.setIssuedAtToNow()
                c.setExpirationTimeMinutesInTheFuture(10f)
                c.issuer = serviceName
                c.setClaim(RequestHashClaim, calculateHash())
            }.toJson()
            jws.key = AesKey(Base64.getDecoder().decode(signingKey))
            jws.algorithmHeaderValue = JwtSigningAlgorithm.HS256.algorithmIdentifier
        }.compactSerialization
    }

    fun decode(nonce: String): JwtClaims {
        return JwtConsumerBuilder()
            .also { b ->
                b.setRequireJwtId()
                b.setRequireExpirationTime()
                b.setExpectedIssuer(serviceName)
                b.setVerificationKey(AesKey(Base64.getDecoder().decode(signingKey)))
            }
            .build()
            .processToClaims(nonce)
    }
}