package io.imulab.astrea.service

import io.imulab.astrea.sdk.flow.CodeRequest
import io.imulab.astrea.sdk.flow.TokenRequest
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.client.OidcClient
import io.imulab.astrea.sdk.oidc.reserved.JweContentEncodingAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.JweKeyManagementAlgorithm

internal val notImplemented: () -> Nothing = { throw NotImplementedError("Not implemented. Should not be called.") }

/**
 * Adapter implementation of [OidcClient] that uses Grpc request payload as client data source. This client can only
 * be used during the authorization leg during the authorize code flow. Properties not included in the request are
 * regarded as 'not needed', hence throws [NotImplementedError] on invocation.
 */
class CodeFlowAuthorizeLegClient(source: CodeRequest.Client) : OidcClient {

    override val id: String = source.id
    override val redirectUris: Set<String> = source.redirectUrisList.toSet()
    override val responseTypes: Set<String> = source.responseTypesList.toSet()
    override val scopes: Set<String> = source.scopesList.toSet()

    // not needed fields
    override val applicationType: String = notImplemented()
    override val contacts: LinkedHashSet<String> = notImplemented()
    override val logoUri: String = notImplemented()
    override val clientUri: String = notImplemented()
    override val policyUri: String = notImplemented()
    override val tosUri: String = notImplemented()
    override val jwksUri: String = notImplemented()
    override val jwks: String = notImplemented()
    override val sectorIdentifierUri: String = notImplemented()
    override val subjectType: String = notImplemented()
    override val idTokenSignedResponseAlgorithm: JwtSigningAlgorithm = notImplemented()
    override val idTokenEncryptedResponseAlgorithm: JweKeyManagementAlgorithm = notImplemented()
    override val idTokenEncryptedResponseEncoding: JweContentEncodingAlgorithm = notImplemented()
    override val requestObjectSigningAlgorithm: JwtSigningAlgorithm = notImplemented()
    override val requestObjectEncryptionAlgorithm: JweKeyManagementAlgorithm = notImplemented()
    override val requestObjectEncryptionEncoding: JweContentEncodingAlgorithm = notImplemented()
    override val userInfoSignedResponseAlgorithm: JwtSigningAlgorithm = notImplemented()
    override val userInfoEncryptedResponseAlgorithm: JweKeyManagementAlgorithm = notImplemented()
    override val userInfoEncryptedResponseEncoding: JweContentEncodingAlgorithm = notImplemented()
    override val tokenEndpointAuthenticationMethod: String = notImplemented()
    override val defaultMaxAge: Long = notImplemented()
    override val requireAuthTime: Boolean = notImplemented()
    override val defaultAcrValues: List<String> = notImplemented()
    override val initiateLoginUri: String = notImplemented()
    override val requestUris: List<String> = notImplemented()
    override val secret: ByteArray = notImplemented()
    override val name: String = notImplemented()
    override val type: String = notImplemented()
    override val grantTypes: Set<String> = notImplemented()
}

/**
 * Adapter implementation of [OidcClient] that uses Grpc request payload as client data source. This client can only
 * be used during the token leg during the authorize code flow. Properties not included in the request are
 * regarded as 'not needed', hence throws [NotImplementedError] on invocation.
 */
class ClientFlowTokenLegClient(source: TokenRequest.Client) : OidcClient {

    override val id: String = source.id
    override val redirectUris: Set<String> = source.redirectUrisList.toSet()
    override val grantTypes: Set<String> = source.grantTypesList.toSet()
    override val jwks: String = source.jwks
    override val sectorIdentifierUri: String = source.sectorIdentifierUri
    override val subjectType: String = source.subjectType
    override val idTokenSignedResponseAlgorithm: JwtSigningAlgorithm =
        JwtSigningAlgorithm.fromSpec(source.idTokenSignedResponseAlgorithm)
    override val idTokenEncryptedResponseAlgorithm: JweKeyManagementAlgorithm =
        JweKeyManagementAlgorithm.fromSpec(source.idTokenEncryptedResponseAlgorithm)
    override val idTokenEncryptedResponseEncoding: JweContentEncodingAlgorithm =
        JweContentEncodingAlgorithm.fromSpec(source.idTokenEncryptedResponseEncoding)

    // not needed fields
    override val applicationType: String = notImplemented()
    override val contacts: LinkedHashSet<String> = notImplemented()
    override val logoUri: String = notImplemented()
    override val clientUri: String = notImplemented()
    override val policyUri: String = notImplemented()
    override val tosUri: String = notImplemented()
    override val jwksUri: String = notImplemented()
    override val requestObjectSigningAlgorithm: JwtSigningAlgorithm = notImplemented()
    override val requestObjectEncryptionAlgorithm: JweKeyManagementAlgorithm = notImplemented()
    override val requestObjectEncryptionEncoding: JweContentEncodingAlgorithm = notImplemented()
    override val userInfoSignedResponseAlgorithm: JwtSigningAlgorithm = notImplemented()
    override val userInfoEncryptedResponseAlgorithm: JweKeyManagementAlgorithm = notImplemented()
    override val userInfoEncryptedResponseEncoding: JweContentEncodingAlgorithm = notImplemented()
    override val tokenEndpointAuthenticationMethod: String = notImplemented()
    override val defaultMaxAge: Long = notImplemented()
    override val requireAuthTime: Boolean = notImplemented()
    override val defaultAcrValues: List<String> = notImplemented()
    override val initiateLoginUri: String = notImplemented()
    override val requestUris: List<String> = notImplemented()
    override val secret: ByteArray = notImplemented()
    override val name: String = notImplemented()
    override val type: String = notImplemented()
    override val responseTypes: Set<String> = notImplemented()
    override val scopes: Set<String> = notImplemented()
}

/**
 * A client whose only fields populated is its id field. This implemented is used to revive request from storage.
 */
class IdOnlyClient(override val id: String) : OidcClient {
    // not needed fields
    override val applicationType: String = notImplemented()
    override val contacts: LinkedHashSet<String> = notImplemented()
    override val logoUri: String = notImplemented()
    override val clientUri: String = notImplemented()
    override val policyUri: String = notImplemented()
    override val tosUri: String = notImplemented()
    override val jwksUri: String = notImplemented()
    override val jwks: String = notImplemented()
    override val sectorIdentifierUri: String = notImplemented()
    override val subjectType: String = notImplemented()
    override val idTokenSignedResponseAlgorithm: JwtSigningAlgorithm = notImplemented()
    override val idTokenEncryptedResponseAlgorithm: JweKeyManagementAlgorithm = notImplemented()
    override val idTokenEncryptedResponseEncoding: JweContentEncodingAlgorithm = notImplemented()
    override val requestObjectSigningAlgorithm: JwtSigningAlgorithm = notImplemented()
    override val requestObjectEncryptionAlgorithm: JweKeyManagementAlgorithm = notImplemented()
    override val requestObjectEncryptionEncoding: JweContentEncodingAlgorithm = notImplemented()
    override val userInfoSignedResponseAlgorithm: JwtSigningAlgorithm = notImplemented()
    override val userInfoEncryptedResponseAlgorithm: JweKeyManagementAlgorithm = notImplemented()
    override val userInfoEncryptedResponseEncoding: JweContentEncodingAlgorithm = notImplemented()
    override val tokenEndpointAuthenticationMethod: String = notImplemented()
    override val defaultMaxAge: Long = notImplemented()
    override val requireAuthTime: Boolean = notImplemented()
    override val defaultAcrValues: List<String> = notImplemented()
    override val initiateLoginUri: String = notImplemented()
    override val requestUris: List<String> = notImplemented()
    override val secret: ByteArray = notImplemented()
    override val name: String = notImplemented()
    override val type: String = notImplemented()
    override val redirectUris: Set<String> = notImplemented()
    override val responseTypes: Set<String> = notImplemented()
    override val grantTypes: Set<String> = notImplemented()
    override val scopes: Set<String> = notImplemented()
}