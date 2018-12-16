package io.imulab.astrea.service.proxy

import org.springframework.session.Session
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

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