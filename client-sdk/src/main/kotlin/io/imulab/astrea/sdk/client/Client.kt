package io.imulab.astrea.sdk.client

import io.imulab.astrea.sdk.oauth.reserved.ResponseType
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.client.OidcClient
import io.imulab.astrea.sdk.oidc.reserved.JweContentEncodingAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.JweKeyManagementAlgorithm
import java.util.*

open class Client(
    override val id: String = UUID.randomUUID().toString(),
    override val name: String,
    override val secret: ByteArray = ByteArray(0),
    override val type: String,
    override val redirectUris: Set<String>,
    override val responseTypes: Set<String>,
    override val grantTypes: Set<String>,
    override val scopes: Set<String>,
    override val applicationType: String,
    override val contacts: LinkedHashSet<String>,
    override val logoUri: String,
    override val clientUri: String,
    override val policyUri: String,
    override val tosUri: String,
    override val jwksUri: String,
    override val jwks: String,
    override val sectorIdentifierUri: String,
    override val subjectType: String,
    override val idTokenSignedResponseAlgorithm: JwtSigningAlgorithm,
    override val idTokenEncryptedResponseAlgorithm: JweKeyManagementAlgorithm,
    override val idTokenEncryptedResponseEncoding: JweContentEncodingAlgorithm,
    override val requestObjectSigningAlgorithm: JwtSigningAlgorithm,
    override val requestObjectEncryptionAlgorithm: JweKeyManagementAlgorithm,
    override val requestObjectEncryptionEncoding: JweContentEncodingAlgorithm,
    override val userInfoSignedResponseAlgorithm: JwtSigningAlgorithm,
    override val userInfoEncryptedResponseAlgorithm: JweKeyManagementAlgorithm,
    override val userInfoEncryptedResponseEncoding: JweContentEncodingAlgorithm,
    override val tokenEndpointAuthenticationMethod: String,
    override val defaultMaxAge: Long,
    override val requireAuthTime: Boolean,
    override val defaultAcrValues: List<String>,
    override val initiateLoginUri: String,
    override val requestUris: List<String>
) : OidcClient {

    init {
        checkEncryptionAlgorithmRelation()
        checkResponseTypeVsIdTokenSigningAlgorithm()
    }

    private fun checkEncryptionAlgorithmRelation() {
        if (requestObjectEncryptionAlgorithm != JweKeyManagementAlgorithm.None)
            check(requestObjectEncryptionEncoding != JweContentEncodingAlgorithm.None) {
                "request_object_encryption_alg and request_object_encryption_enc must both be set."
            }
        if (userInfoEncryptedResponseAlgorithm != JweKeyManagementAlgorithm.None)
            check(userInfoEncryptedResponseEncoding != JweContentEncodingAlgorithm.None) {
                "userinfo_encrypted_response_alg and userinfo_encrypted_response_enc must both be set."
            }
        if (idTokenEncryptedResponseAlgorithm != JweKeyManagementAlgorithm.None)
            check(idTokenEncryptedResponseEncoding != JweContentEncodingAlgorithm.None) {
                "id_token_encrypted_response_alg and id_token_encrypted_response_enc must both be set."
            }
    }

    private fun checkResponseTypeVsIdTokenSigningAlgorithm() {
        if (idTokenSignedResponseAlgorithm == JwtSigningAlgorithm.None)
            check(!responseTypes.contains(ResponseType.code)) {
                """
                    Client selected response_type=code which is bound to use token endpoint for token issuance.
                    However, if client needs to use token endpoint, id_token_signed_response_alg cannot be none.
                """.trimIndent()
            }
    }
}