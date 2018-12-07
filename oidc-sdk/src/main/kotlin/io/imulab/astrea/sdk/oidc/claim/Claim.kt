package io.imulab.astrea.sdk.oidc.claim

import io.imulab.astrea.sdk.oidc.reserved.IdTokenClaim
import java.util.*

data class Claim(
    val name: String,
    val source: String,
    val essential: Boolean = false,
    val values: List<String> = emptyList()
) {

    fun toMap(): Map<String, Any> = mapOf(
        "name" to name,
        "source" to source,
        "essential" to essential,
        "values" to values
    )
}

class Claims(map: Map<String, Any> = emptyMap()) {

    constructor(claims: List<io.imulab.astrea.sdk.oidc.claim.Claim>) : this() {
        this.claims.addAll(claims)
    }

    private val claims: MutableList<io.imulab.astrea.sdk.oidc.claim.Claim> = LinkedList<io.imulab.astrea.sdk.oidc.claim.Claim>().apply {
        listOf("userinfo", "id_token").forEach { source ->
            (map[source] as? Map<*, *>)?.entries
                ?.map { entry ->
                    val name = entry.key as? String ?: return@map null
                    val essential = (entry.value as? Map<*, *>)?.get("essential")?.toString()?.toBoolean() ?: false
                    val values = (entry.value as? Map<*, *>)?.get("value")?.toString()?.let { listOf(it) }
                        ?: ((entry.value as? Map<*, *>)?.get("values") as? List<*>)?.map { it?.toString() ?: "" }
                        ?: emptyList()
                    return@map io.imulab.astrea.sdk.oidc.claim.Claim(
                        name,
                        source,
                        essential,
                        values.filter { it.isNotEmpty() })
                }?.forEach {
                    if (it != null)
                        add(it)
                }
        }
    }

    fun getClaim(name: String): io.imulab.astrea.sdk.oidc.claim.Claim? = claims.firstOrNull { it.name == name }

    fun getAllClaims(): List<io.imulab.astrea.sdk.oidc.claim.Claim> = claims

    fun requireAuthTime() {
        if (getClaim(IdTokenClaim.authTime) == null)
            addClaim(io.imulab.astrea.sdk.oidc.claim.Claim(IdTokenClaim.authTime, "id_token", true, emptyList()))
    }

    private fun addClaim(claim: io.imulab.astrea.sdk.oidc.claim.Claim) {
        this.claims.add(claim)
    }

    fun isEmpty(): Boolean = claims.isEmpty()

    fun isNotEmpty(): Boolean = !isEmpty()
}

interface ClaimConverter {

    /**
     * Convert claims JSON string to map structure. Implementations should raise
     * invalid_request error failed.
     */
    fun fromJson(json: String): io.imulab.astrea.sdk.oidc.claim.Claims
}