package io.imulab.astrea.service.authorize

import com.fasterxml.jackson.module.kotlin.readValue
import com.typesafe.config.Config
import io.imulab.astrea.sdk.oauth.error.InvalidRequest
import io.imulab.astrea.sdk.oauth.reserved.AuthenticationMethod
import io.imulab.astrea.sdk.oidc.claim.ClaimConverter
import io.imulab.astrea.sdk.oidc.claim.Claims
import io.imulab.astrea.sdk.oidc.discovery.Discovery
import io.imulab.astrea.sdk.oidc.discovery.OidcContext
import io.imulab.astrea.sdk.oidc.jwk.JsonWebKeySetRepository
import io.imulab.astrea.sdk.oidc.jwk.JsonWebKeySetStrategy
import io.imulab.astrea.sdk.oidc.reserved.OidcParam
import io.imulab.astrea.sdk.oidc.spi.HttpResponse
import io.imulab.astrea.sdk.oidc.spi.SimpleHttpClient
import io.vertx.core.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jose4j.jwk.JsonWebKeySet
import org.jose4j.keys.AesKey
import java.security.Key
import java.time.Duration
import java.util.*

/**
 * Service wide configuration.
 */
class ServiceContext(config: Config, discovery: Discovery) : OidcContext, Discovery by discovery {
    override val masterJsonWebKeySet: JsonWebKeySet = JsonWebKeySet(config.getString("service.jwks"))
    override val issuerUrl: String = issuer
    override val authorizeEndpointUrl: String = authorizationEndpoint
    override val tokenEndpointUrl: String = tokenEndpoint
    override val defaultTokenEndpointAuthenticationMethod: String = AuthenticationMethod.clientSecretBasic
    override val authorizeCodeLifespan: Duration = config.getDuration("service.authorizeCodeLifespan")
    override val accessTokenLifespan: Duration = config.getDuration("service.accessTokenLifespan")
    override val refreshTokenLifespan: Duration = config.getDuration("service.refreshTokenLifespan")
    override val idTokenLifespan: Duration = config.getDuration("service.idTokenLifespan")
    override val stateEntropy: Int = config.getInt("service.stateEntropy")
    override val nonceEntropy: Int = config.getInt("service.nonceEntropy")

    val authorizeCodeKey: Key = AesKey(Base64.getDecoder().decode(config.getString("service.authorizeCodeKey")))

    override fun validate() {
        super<OidcContext>.validate()
    }
}

/**
 * Service provider implementation of [SimpleHttpClient] using OkHttp.
 */
object OkHttpClient : SimpleHttpClient {

    override suspend fun get(url: String): HttpResponse {
        val response = OkHttpClient().newCall(Request.Builder().url(url).build()).execute()
        return object : HttpResponse {
            override fun status(): Int = response.code()
            override fun body(): String = response.body()?.string() ?: ""
        }
    }
}

/**
 * Placeholder implementation for [SimpleHttpClient].
 */
object NoOpHttpClient : SimpleHttpClient {
    override suspend fun get(url: String): HttpResponse { notImplemented() }
}

/**
 * Converter for `claim` parameter using Vertx's main jackson object mapper.
 */
object JacksonClaimConverter: ClaimConverter {
    override fun fromJson(json: String): Claims {
        return try {
            Claims(Json.mapper.readValue<LinkedHashMap<String, Any>>(json))
        } catch (e: Exception) {
            throw InvalidRequest.invalid(OidcParam.claims)
        }
    }
}

/**
 * Implementation of [JsonWebKeySetRepository] to provide a server Json Web Key Set determined at startup time, and
 * does not implement any of the client Json Web Key Set logic because client key sets are supposed to be returned
 * by the client lookup service.
 */
internal class LocalJsonWebKeySetRepository(private val serverJwks: JsonWebKeySet) : JsonWebKeySetRepository {
    override suspend fun getServerJsonWebKeySet(): JsonWebKeySet = serverJwks
    override suspend fun getClientJsonWebKeySet(jwksUri: String): JsonWebKeySet? = null
    override suspend fun writeClientJsonWebKeySet(jwksUri: String, keySet: JsonWebKeySet) {}
}

/**
 * Extension to [JsonWebKeySetStrategy] to resolve all keys locally.
 */
class LocalJsonWebKeySetStrategy(serverJwks: JsonWebKeySet) : JsonWebKeySetStrategy(
    httpClient = NoOpHttpClient,
    jsonWebKeySetRepository = LocalJsonWebKeySetRepository(serverJwks)
)

internal val notImplemented : () -> Nothing = {
    throw UnsupportedOperationException("should not be called.")
}
