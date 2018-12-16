package io.imulab.astrea.service.proxy

import org.springframework.session.MapSession
import org.springframework.session.Session
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

class LoginSession(private val mapSession: Session = MapSession()) : Session by mapSession {

    companion object {
        const val AuthExpiry = "auth_exp"
        const val AuthSubject = "auth_subj"
        const val AuthTime = "auth_time"
    }

    var authenticationExpiry: LocalDateTime
        get() = LocalDateTime.ofEpochSecond(mapSession.getAttributeOrDefault(AuthExpiry, 0), 0, ZoneOffset.UTC)
        set(value) {
            mapSession.setAttribute(AuthExpiry, value.toEpochSecond(ZoneOffset.UTC))
            mapSession.maxInactiveInterval = Duration.between(LocalDateTime.now(ZoneOffset.UTC), value)
        }

    var subject: String
        get() = mapSession.getAttributeOrDefault(AuthSubject, "")
        set(value) {
            mapSession.setAttribute(AuthSubject, value)
        }

    var authTime: LocalDateTime
        get() = LocalDateTime.ofEpochSecond(mapSession.getAttributeOrDefault(AuthTime, 0), 0, ZoneOffset.UTC)
        set(value) {
            mapSession.setAttribute(AuthTime, value.toEpochSecond(ZoneOffset.UTC))
        }

    fun hasAuthenticationExpired(): Boolean =
        authenticationExpiry.isBefore(LocalDateTime.now(ZoneOffset.UTC))
}