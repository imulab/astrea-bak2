package io.imulab.astrea.service.authorization.authz.authn.session

/**
 * Interface for managing authentication sessions.
 */
interface AuthenticationSessionStrategy {

    suspend fun retrieveAuthentication(call: Any): AuthenticationSession?

    suspend fun writeAuthentication(call: Any, session: AuthenticationSession)
}