package io.imulab.astrea.sdk.client

import io.imulab.astrea.sdk.oauth.reserved.AuthenticationMethod
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.client.OidcClient
import io.imulab.astrea.sdk.oidc.reserved.JweContentEncodingAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.JweKeyManagementAlgorithm
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.LinkedHashSet

data class Client(
    override var id: String = UUID.randomUUID().toString(),
    var creationTime: LocalDateTime = LocalDateTime.now(),
    var clientName: String = "",
    var clientSecret: String = "",
    var clientType: String = "",
    override var redirectUris: MutableSet<String> = mutableSetOf(),
    override var responseTypes: MutableSet<String> = mutableSetOf(),
    override var grantTypes: MutableSet<String> = mutableSetOf(),
    override var scopes: MutableSet<String> = mutableSetOf(),
    override var applicationType: String = "",
    override var contacts: LinkedHashSet<String> = LinkedHashSet(),
    override var logoUri: String = "",
    override var clientUri: String = "",
    override var policyUri: String = "",
    override var tosUri: String = "",
    override var jwksUri: String = "",
    override var jwks: String = "",
    override var sectorIdentifierUri: String = "",
    override var subjectType: String = "",
    var idTokenSignedResponseAlg: String = JwtSigningAlgorithm.RS256.spec,
    var idTokenEncryptedResponseAlg: String = JweKeyManagementAlgorithm.None.spec,
    var idTokenEncryptedResponseEnc: String = JweContentEncodingAlgorithm.None.spec,
    var requestObjectSigningAlg: String = JwtSigningAlgorithm.None.spec,
    var requestObjectEncryptionAlg: String = JweKeyManagementAlgorithm.None.spec,
    var requestObjectEncryptionEnc: String = JweContentEncodingAlgorithm.None.spec,
    var userinfoSignedResponseAlg: String = JwtSigningAlgorithm.None.spec,
    var userinfoEncryptedResponseAlg: String = JweKeyManagementAlgorithm.None.spec,
    var userinfoEncryptedResponseEnc: String = JweContentEncodingAlgorithm.None.spec,
    var tokenEndpointAuthMethod: String = AuthenticationMethod.clientSecretBasic,
    override var defaultMaxAge: Long = 0,
    override var requireAuthTime: Boolean = false,
    override var defaultAcrValues: MutableList<String> = mutableListOf(),
    override var initiateLoginUri: String = "",
    override var requestUris: MutableList<String> = mutableListOf(),
    var requests: MutableMap<String, String> = mutableMapOf()
) : OidcClient {

    override val name: String
        get() = clientName
    override val secret: ByteArray
        get() = clientSecret.toByteArray()
    override val type: String
        get() = clientType
    override val idTokenSignedResponseAlgorithm: JwtSigningAlgorithm
        get() = JwtSigningAlgorithm.fromSpec(idTokenSignedResponseAlg)
    override val idTokenEncryptedResponseAlgorithm: JweKeyManagementAlgorithm
        get() = JweKeyManagementAlgorithm.fromSpec(idTokenEncryptedResponseAlg)
    override val idTokenEncryptedResponseEncoding: JweContentEncodingAlgorithm
        get() = JweContentEncodingAlgorithm.fromSpec(idTokenEncryptedResponseEnc)
    override val requestObjectSigningAlgorithm: JwtSigningAlgorithm
        get() = JwtSigningAlgorithm.fromSpec(requestObjectSigningAlg)
    override val requestObjectEncryptionAlgorithm: JweKeyManagementAlgorithm
        get() = JweKeyManagementAlgorithm.fromSpec(requestObjectEncryptionAlg)
    override val requestObjectEncryptionEncoding: JweContentEncodingAlgorithm
        get() = JweContentEncodingAlgorithm.fromSpec(requestObjectEncryptionEnc)
    override val userInfoSignedResponseAlgorithm: JwtSigningAlgorithm
        get() = JwtSigningAlgorithm.fromSpec(userinfoSignedResponseAlg)
    override val userInfoEncryptedResponseAlgorithm: JweKeyManagementAlgorithm
        get() = JweKeyManagementAlgorithm.fromSpec(userinfoEncryptedResponseAlg)
    override val userInfoEncryptedResponseEncoding: JweContentEncodingAlgorithm
        get() = JweContentEncodingAlgorithm.fromSpec(userinfoEncryptedResponseEnc)
    override val tokenEndpointAuthenticationMethod: String
        get() = tokenEndpointAuthMethod

    companion object {
        fun fromClientLookupResponse(response: ClientLookupResponse): Client {
            return Client(
                id = response.id,
                clientName = response.name,
                clientType = response.type,
                redirectUris = response.redirectUrisList.toMutableSet(),
                responseTypes = response.responseTypesList.toMutableSet(),
                grantTypes = response.grantTypesList.toMutableSet(),
                scopes = response.scopesList.toMutableSet(),
                applicationType = response.applicationType,
                contacts = LinkedHashSet(response.contactsList),
                logoUri = response.logoUri,
                clientUri = response.clientUri,
                policyUri = response.policyUri,
                tosUri = response.tosUri,
                jwksUri = "",
                jwks = response.jwks,
                sectorIdentifierUri = response.sectorIdentifierUri,
                subjectType = response.subjectType,
                idTokenSignedResponseAlg = response.idTokenSignedResponseAlgorithm,
                idTokenEncryptedResponseAlg = response.idTokenEncryptedResponseAlgorithm,
                idTokenEncryptedResponseEnc = response.idTokenEncryptedResponseEncoding,
                requestObjectSigningAlg = response.requestObjectSigningAlgorithm,
                requestObjectEncryptionAlg = response.requestObjectEncryptionAlgorithm,
                requestObjectEncryptionEnc = response.requestObjectEncryptionEncoding,
                userinfoSignedResponseAlg = response.userInfoSignedResponseAlgorithm,
                userinfoEncryptedResponseAlg = response.userInfoEncryptedResponseAlgorithm,
                userinfoEncryptedResponseEnc = response.userInfoEncryptedResponseEncoding,
                tokenEndpointAuthMethod = response.tokenEndpointAuthenticationMethod,
                defaultMaxAge = response.defaultMaxAge,
                requireAuthTime = response.requireAuthTime,
                defaultAcrValues = response.defaultAcrValuesList,
                initiateLoginUri = response.initiateLoginUri,
                requestUris = response.requestUrisList
            )
        }
    }
}