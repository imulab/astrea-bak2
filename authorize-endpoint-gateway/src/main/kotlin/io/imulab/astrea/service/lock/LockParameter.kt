package io.imulab.astrea.service.lock

import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.service.authn.parameterLoginToken
import io.vertx.ext.web.RoutingContext
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import java.security.Key
import java.security.MessageDigest
import java.util.*

internal const val parameterLockParam = "param_lock"

/**
 * Utility to handle parameter locking related logic.
 *
 * A parameter lock is a JWT signed token containing a SHA-256 of the requested parameters as well as a stage record as
 * how far into the authentication/authorization processing did the last request go. Its purpose is to prevent external
 * authentication/consent providers from tempering with request parameters sent by client without having to explicitly
 * cache the request (causing state management) locally at the gateway.
 */
class ParameterLocker(
    private val reservedParams: List<String> = listOf(
        parameterLockParam,
        parameterLoginToken,
        "consent_token"
    ),
    private val serviceName: String,
    private val lockKey: Key
) {

    private val paramHash = "paramHash"
    private val stage = "stage"
    private val authenticationStage = 1
    private val authorizationStage = 2
    private val verifiedLock = "verifiedLock"

    /**
     * Perform an SHA-256 hash on the joined string representation of the sorted parameter map, and set the hash
     * on the routing context.
     */
    fun hashParameters(rc: RoutingContext) {
        val lock = rc.request().params()
            .filterNot { p -> reservedParams.contains(p.key) }
            .sortedBy { p -> p.key }
            .joinToString { p -> p.key + ":" + p.value }
            .let { s -> MessageDigest.getInstance("SHA-256").digest(s.toByteArray()) }
            .let { b -> Base64.getEncoder().withoutPadding().encodeToString(b) }
        rc.put(paramHash, lock)
    }

    /**
     * Verify that the parameter lock presented in the request was issued by me, and not altered in any way. Once
     * verified, set the exploded claim data on routing context.
     */
    fun verifyParameterLock(rc: RoutingContext) {
        val lock = rc.request().getParam(parameterLockParam)
        if (lock.isNullOrEmpty())
            return

        rc.put(verifiedLock, verifyLock(lock))
    }

    /**
     * Perform JWT verification on lock and return claims.
     */
    private fun verifyLock(lock: String): JwtClaims {
        return JwtConsumerBuilder()
            .also { b ->
                b.setRequireJwtId()
                b.setRequireExpirationTime()
                b.setExpectedIssuer(serviceName)
                b.setVerificationKey(lockKey)
            }
            .build()
            .processToClaims(lock)
    }

    /**
     * Create lock parameter, with stage claim set to authentication stage.
     */
    fun createLockForAuthenticationStage(rc: RoutingContext): String =
        createLockForStage(rc, authenticationStage)

    /**
     * Create lock parameter, with stage claim set to authorization stage.
     */
    fun createLockForAuthorizationStage(rc: RoutingContext): String =
        createLockForStage(rc, authorizationStage)

    /**
     * Create lock parameter for a given stage claim. This method requires parameter hash
     * already been set on routing context. If satisfied, it set the parameter hash claim and
     * stage claim into the JWT and signs it with a lock key.
     */
    private fun createLockForStage(rc: RoutingContext, stageIndex: Int): String {
        val hash = rc.get<String>(paramHash)
            ?: throw IllegalStateException("param hash must exist in context.")

        return JsonWebSignature().also { jws ->
            jws.payload = JwtClaims().also { c ->
                c.setGeneratedJwtId()
                c.setIssuedAtToNow()
                c.setExpirationTimeMinutesInTheFuture(10f)
                c.issuer = serviceName
                c.setClaim(paramHash, hash)
                c.setClaim(stage, stageIndex)
            }.toJson()
            jws.key = lockKey
            jws.algorithmHeaderValue = JwtSigningAlgorithm.HS256.algorithmIdentifier
        }.compactSerialization
    }

    /**
     * Returns true if the stage claim from the lock parameter JWT is at or beyond authentication
     * stage.
     */
    fun hasVisitedAuthenticationBefore(rc: RoutingContext): Boolean {
        val lock = rc.request().getParam(parameterLockParam)
        if (lock.isNullOrEmpty())
            return false

        val verifiedLock = rc.get<JwtClaims>(verifiedLock) ?: verifyLock(lock)
        return verifiedLock.getStringClaimValue(stage).toInt() >= authenticationStage
    }

    /**
     * Returns true if the stage claim from the lock parameter JWT is at or beyond authorization
     * stage.
     */
    fun hasVisitedAuthorizationBefore(rc: RoutingContext): Boolean {
        val lock = rc.request().getParam(parameterLockParam)
        if (lock.isNullOrEmpty())
            return false

        val verifiedLock = rc.get<JwtClaims>(verifiedLock) ?: verifyLock(lock)
        return verifiedLock.getStringClaimValue(stage).toInt() >= authorizationStage
    }
}