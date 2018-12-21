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
    override val applicationType: String by lazy { notImplemented() }
    override val contacts: LinkedHashSet<String> by lazy { notImplemented() }
    override val logoUri: String by lazy { notImplemented() }
    override val clientUri: String by lazy { notImplemented() }
    override val policyUri: String by lazy { notImplemented() }
    override val tosUri: String by lazy { notImplemented() }
    override val jwksUri: String by lazy { notImplemented() }
    override val jwks: String by lazy { notImplemented() }
    override val sectorIdentifierUri: String by lazy { notImplemented() }
    override val subjectType: String by lazy { notImplemented() }
    override val idTokenSignedResponseAlgorithm: JwtSigningAlgorithm by lazy { notImplemented() }
    override val idTokenEncryptedResponseAlgorithm: JweKeyManagementAlgorithm by lazy { notImplemented() }
    override val idTokenEncryptedResponseEncoding: JweContentEncodingAlgorithm by lazy { notImplemented() }
    override val requestObjectSigningAlgorithm: JwtSigningAlgorithm by lazy { notImplemented() }
    override val requestObjectEncryptionAlgorithm: JweKeyManagementAlgorithm by lazy { notImplemented() }
    override val requestObjectEncryptionEncoding: JweContentEncodingAlgorithm by lazy { notImplemented() }
    override val userInfoSignedResponseAlgorithm: JwtSigningAlgorithm by lazy { notImplemented() }
    override val userInfoEncryptedResponseAlgorithm: JweKeyManagementAlgorithm by lazy { notImplemented() }
    override val userInfoEncryptedResponseEncoding: JweContentEncodingAlgorithm by lazy { notImplemented() }
    override val tokenEndpointAuthenticationMethod: String by lazy { notImplemented() }
    override val defaultMaxAge: Long by lazy { notImplemented() }
    override val requireAuthTime: Boolean by lazy { notImplemented() }
    override val defaultAcrValues: List<String> by lazy { notImplemented() }
    override val initiateLoginUri: String by lazy { notImplemented() }
    override val requestUris: List<String> by lazy { notImplemented() }
    override val secret: ByteArray by lazy { notImplemented() }
    override val name: String by lazy { notImplemented() }
    override val type: String by lazy { notImplemented() }
    override val grantTypes: Set<String> by lazy { notImplemented() }
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
    override val applicationType: String by lazy { notImplemented() }
    override val contacts: LinkedHashSet<String> by lazy { notImplemented() }
    override val logoUri: String by lazy { notImplemented() }
    override val clientUri: String by lazy { notImplemented() }
    override val policyUri: String by lazy { notImplemented() }
    override val tosUri: String by lazy { notImplemented() }
    override val jwksUri: String by lazy { notImplemented() }
    override val requestObjectSigningAlgorithm: JwtSigningAlgorithm by lazy { notImplemented() }
    override val requestObjectEncryptionAlgorithm: JweKeyManagementAlgorithm by lazy { notImplemented() }
    override val requestObjectEncryptionEncoding: JweContentEncodingAlgorithm by lazy { notImplemented() }
    override val userInfoSignedResponseAlgorithm: JwtSigningAlgorithm by lazy { notImplemented() }
    override val userInfoEncryptedResponseAlgorithm: JweKeyManagementAlgorithm by lazy { notImplemented() }
    override val userInfoEncryptedResponseEncoding: JweContentEncodingAlgorithm by lazy { notImplemented() }
    override val tokenEndpointAuthenticationMethod: String by lazy { notImplemented() }
    override val defaultMaxAge: Long by lazy { notImplemented() }
    override val requireAuthTime: Boolean by lazy { notImplemented() }
    override val defaultAcrValues: List<String> by lazy { notImplemented() }
    override val initiateLoginUri: String by lazy { notImplemented() }
    override val requestUris: List<String> by lazy { notImplemented() }
    override val secret: ByteArray by lazy { notImplemented() }
    override val name: String by lazy { notImplemented() }
    override val type: String by lazy { notImplemented() }
    override val responseTypes: Set<String> by lazy { notImplemented() }
    override val scopes: Set<String> by lazy { notImplemented() }
}

/**
 * A client whose only fields populated is its id field. This implemented is used to revive request from storage.
 */
class IdOnlyClient(override val id: String) : OidcClient {
    // not needed fields
    override val applicationType: String by lazy { notImplemented() }
    override val contacts: LinkedHashSet<String> by lazy { notImplemented() }
    override val logoUri: String by lazy { notImplemented() }
    override val clientUri: String by lazy { notImplemented() }
    override val policyUri: String by lazy { notImplemented() }
    override val tosUri: String by lazy { notImplemented() }
    override val jwksUri: String by lazy { notImplemented() }
    override val jwks: String by lazy { notImplemented() }
    override val sectorIdentifierUri: String by lazy { notImplemented() }
    override val subjectType: String by lazy { notImplemented() }
    override val idTokenSignedResponseAlgorithm: JwtSigningAlgorithm by lazy { notImplemented() }
    override val idTokenEncryptedResponseAlgorithm: JweKeyManagementAlgorithm by lazy { notImplemented() }
    override val idTokenEncryptedResponseEncoding: JweContentEncodingAlgorithm by lazy { notImplemented() }
    override val requestObjectSigningAlgorithm: JwtSigningAlgorithm by lazy { notImplemented() }
    override val requestObjectEncryptionAlgorithm: JweKeyManagementAlgorithm by lazy { notImplemented() }
    override val requestObjectEncryptionEncoding: JweContentEncodingAlgorithm by lazy { notImplemented() }
    override val userInfoSignedResponseAlgorithm: JwtSigningAlgorithm by lazy { notImplemented() }
    override val userInfoEncryptedResponseAlgorithm: JweKeyManagementAlgorithm by lazy { notImplemented() }
    override val userInfoEncryptedResponseEncoding: JweContentEncodingAlgorithm by lazy { notImplemented() }
    override val tokenEndpointAuthenticationMethod: String by lazy { notImplemented() }
    override val defaultMaxAge: Long by lazy { notImplemented() }
    override val requireAuthTime: Boolean by lazy { notImplemented() }
    override val defaultAcrValues: List<String> by lazy { notImplemented() }
    override val initiateLoginUri: String by lazy { notImplemented() }
    override val requestUris: List<String> by lazy { notImplemented() }
    override val secret: ByteArray by lazy { notImplemented() }
    override val name: String by lazy { notImplemented() }
    override val type: String by lazy { notImplemented() }
    override val redirectUris: Set<String> by lazy { notImplemented() }
    override val responseTypes: Set<String> by lazy { notImplemented() }
    override val grantTypes: Set<String> by lazy { notImplemented() }
    override val scopes: Set<String> by lazy { notImplemented() }
}