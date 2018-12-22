package io.imulab.astrea.service

import com.fasterxml.jackson.module.kotlin.readValue
import io.imulab.astrea.sdk.oauth.error.InvalidRequest
import io.imulab.astrea.sdk.oidc.claim.ClaimConverter
import io.imulab.astrea.sdk.oidc.claim.Claims
import io.imulab.astrea.sdk.oidc.reserved.OidcParam
import io.vertx.core.json.Json
import java.util.LinkedHashMap

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