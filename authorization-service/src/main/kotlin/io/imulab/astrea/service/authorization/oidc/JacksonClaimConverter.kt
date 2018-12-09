package io.imulab.astrea.service.authorization.oidc

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.imulab.astrea.sdk.oauth.error.InvalidRequest
import io.imulab.astrea.sdk.oidc.claim.ClaimConverter
import io.imulab.astrea.sdk.oidc.claim.Claims
import io.imulab.astrea.sdk.oidc.reserved.OidcParam

class JacksonClaimConverter(private val objectMapper: ObjectMapper): ClaimConverter {

    override fun fromJson(json: String): Claims {
        return try {
            Claims(objectMapper.readValue<LinkedHashMap<String, Any>>(json))
        } catch (e: Exception) {
            throw InvalidRequest.invalid(OidcParam.claims)
        }
    }
}