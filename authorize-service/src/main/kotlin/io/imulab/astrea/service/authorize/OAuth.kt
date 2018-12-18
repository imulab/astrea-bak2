package io.imulab.astrea.service.authorize

import io.imulab.astrea.sdk.oauth.assertType
import io.imulab.astrea.sdk.oauth.request.OAuthRequest
import io.imulab.astrea.sdk.oauth.token.storage.AccessTokenRepository
import io.imulab.astrea.sdk.oauth.token.storage.AuthorizeCodeRepository
import io.imulab.astrea.sdk.oauth.token.storage.RefreshTokenRepository
import io.imulab.astrea.sdk.oauth.token.strategy.RefreshTokenStrategy
import io.imulab.astrea.sdk.oidc.request.OidcAuthorizeRequest
import io.imulab.astrea.sdk.oidc.request.OidcSession
import io.imulab.astrea.sdk.oidc.request.OidcSessionRepository
import io.vertx.core.json.Json
import io.vertx.kotlin.redis.hsetAwait
import io.vertx.redis.RedisClient
import java.time.ZoneOffset

/**
 * Implementation of [AuthorizeCodeRepository] that only cares about saving authorize code session, because
 * this service does not consume authorize code.
 */
class RedisAuthorizeCodeRepository(private val redisClient: RedisClient) : AuthorizeCodeRepository, OidcSessionRepository {

    private companion object {
        const val AuthorizeCodeHash = "auth"
        const val OidcSessionHash = "oidc"
    }

    // todo: consider expiry, replace hset with top level set

    override suspend fun getAuthorizeCodeSession(code: String): OAuthRequest = notImplemented()
    override suspend fun invalidateAuthorizeCodeSession(code: String) = notImplemented()
    override suspend fun getOidcSession(authorizeCode: String): OidcSession = notImplemented()
    override suspend fun deleteOidcSession(authorizeCode: String) = notImplemented()

    override suspend fun createAuthorizeCodeSession(code: String, request: OAuthRequest) {
        redisClient.hsetAwait(AuthorizeCodeHash, code, Json.encode(request.assertType<OidcAuthorizeRequest>().toMap()))
    }

    override suspend fun createOidcSession(authorizeCode: String, session: OidcSession) {
        redisClient.hsetAwait(OidcSessionHash, authorizeCode, Json.encode(session.toMap()))
    }

    private fun OidcAuthorizeRequest.toMap(): Map<String, Any> {
        return HashMap<String, Any>().apply {
            put("orig_req_id", id)
            put("orig_req_time", requestTime.toEpochSecond(ZoneOffset.UTC))
            put("client", client.id)
            putAll(session.assertType<OidcSession>().toMap())
        }
    }

    private fun OidcSession.toMap(): Map<String, Any> = mapOf(
        "scopes" to grantedScopes,
        "sub" to subject,
        "ofs_sub" to obfuscatedSubject,
        "acr" to acrValues,
        "nonce" to nonce,
        "id_tok" to idTokenClaims,
        "acc_tok" to accessTokenClaims
    )
}

/**
 * NoOp implementation of [AccessTokenRepository] because this service uses JWT based stateless access token.
 */
object NoOpAccessTokenRepository : AccessTokenRepository {
    override suspend fun createAccessTokenSession(token: String, request: OAuthRequest) {}
    override suspend fun getAccessTokenSession(token: String): OAuthRequest = notImplemented()
    override suspend fun deleteAccessTokenSession(token: String) {}
    override suspend fun deleteAccessTokenAssociatedWithRequest(requestId: String) {}
}

/**
 * Exception throwing implementation of [RefreshTokenRepository] because this service does not issue refresh token.
 */
object NotImplementedRefreshTokenRepository : RefreshTokenRepository {
    override suspend fun createRefreshTokenSession(token: String, request: OAuthRequest) { notImplemented() }
    override suspend fun getRefreshTokenSession(token: String): OAuthRequest = notImplemented()
    override suspend fun deleteRefreshTokenSession(token: String) { notImplemented() }
    override suspend fun deleteRefreshTokenAssociatedWithRequest(requestId: String) { notImplemented() }
}

/**
 * Exception throwing implementation of [RefreshTokenStrategy] because this service does not issue refresh token.
 */
object NotImplementedRefreshTokenStrategy : RefreshTokenStrategy {
    override fun computeIdentifier(token: String): String = notImplemented()
    override suspend fun generateToken(request: OAuthRequest): String = notImplemented()
    override suspend fun verifyToken(token: String, request: OAuthRequest) { notImplemented() }
}