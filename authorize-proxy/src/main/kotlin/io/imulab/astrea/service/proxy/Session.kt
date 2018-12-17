package io.imulab.astrea.service.proxy

import org.jose4j.jwt.JwtClaims
import org.springframework.session.Session
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

// Login session
internal const val AuthSessionName = "ASTREA_AUTH_SESSION"
private const val AuthExpiry = "auth_exp"
private const val AuthSubject = "auth_subj"
private const val AuthTime = "auth_time"

internal var Session.authenticationExpiry: LocalDateTime
    get() = LocalDateTime.ofEpochSecond(getAttributeOrDefault(AuthExpiry, 0), 0, ZoneOffset.UTC)
    set(value) {
        setAttribute(AuthExpiry, value.toEpochSecond(ZoneOffset.UTC))
        maxInactiveInterval = Duration.between(LocalDateTime.now(ZoneOffset.UTC), value)
    }

internal var Session.subject: String
    get() = getAttributeOrDefault(AuthSubject, "")
    set(value) {
        setAttribute(AuthSubject, value)
    }

internal var Session.authTime: LocalDateTime
    get() = LocalDateTime.ofEpochSecond(getAttributeOrDefault(AuthTime, 0), 0, ZoneOffset.UTC)
    set(value) {
        setAttribute(AuthTime, value.toEpochSecond(ZoneOffset.UTC))
    }

fun Session.hasAuthenticationExpired(): Boolean =
    authenticationExpiry.isBefore(LocalDateTime.now(ZoneOffset.UTC))

// Consent session
internal const val ConsentSessionName = "ASTREA_CONSENT_SESSION"
private const val ConsentExpiry = "c_exp"
private const val ConsentScope = "c_scope"
private const val ConsentClaims = "c_claim"

internal var Session.consentExpiry: LocalDateTime
    get() = LocalDateTime.ofEpochSecond(getAttributeOrDefault(ConsentExpiry, 0), 0, ZoneOffset.UTC)
    set(value) {
        setAttribute(ConsentExpiry, value.toEpochSecond(ZoneOffset.UTC))
        maxInactiveInterval = Duration.between(LocalDateTime.now(ZoneOffset.UTC), value)
    }

internal var Session.consentScope: Set<String>
    get() = getAttributeOrDefault(ConsentScope, emptySet())
    set(value) {
        setAttribute(ConsentScope, value)
    }

internal var Session.consentClaims: Map<String, Any>
    get() = getAttributeOrDefault(ConsentClaims, emptyMap())
    set(value) {
        setAttribute(ConsentClaims, value)
    }

fun Session.hasConsentExpired(): Boolean =
        consentExpiry.isBefore(LocalDateTime.now(ZoneOffset.UTC))

// helper
internal fun JwtClaims.rememberFor(): Duration {
    return if (!hasClaim("remember_for"))
        Duration.ZERO
    else
        getStringClaimValue("remember_for").toLongOrNull()
            ?.let { Duration.ofSeconds(it) }
            ?: Duration.ZERO
}

@Suppress("unchecked_cast")
internal fun JwtClaims.getUserClaims(): Map<String, Any> {
    return getClaimValue("claims") as? Map<String, Any> ?: emptyMap()
}