package io.imulab.astrea.service.authorization.authz.authn.session

import java.time.LocalDateTime

/**
 * Session data to be stored as proof of authentication.
 */
data class AuthenticationSession(
    val subject: String,
    val authTime: LocalDateTime,
    val expiry: LocalDateTime
)