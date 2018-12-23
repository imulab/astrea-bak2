package io.imulab.astrea.service

import com.fasterxml.jackson.module.kotlin.readValue
import io.imulab.astrea.sdk.oauth.error.InvalidRequest
import io.imulab.astrea.sdk.oidc.claim.ClaimConverter
import io.imulab.astrea.sdk.oidc.claim.Claims
import io.imulab.astrea.sdk.oidc.reserved.OidcParam
import io.vertx.core.json.Json
import java.util.LinkedHashMap

object RoutingContextAttribute {
    const val authentication = "authentication"
    const val authorization = "authorization"
    const val parameterHash = "param_hash"
    const val verifiedParameterLock = "verified_param_lock"
}

object Stage {
    const val name = "stage"
    const val authentication = 1
    const val authorization = 2
}

object Params {
    const val parameterLock = "param_lock"
    const val loginToken = "login_token"
    const val consentToken = "consent_token"
}

/**
 * Converter for `claim` parameter using Vertx's main jackson object mapper.
 */
object JacksonClaimConverter: ClaimConverter {
    override fun fromJson(json: String): Claims {
        return try {
            Claims(
                Json.mapper.readValue<LinkedHashMap<String, Any>>(
                    json
                )
            )
        } catch (e: Exception) {
            throw InvalidRequest.invalid(OidcParam.claims)
        }
    }
}