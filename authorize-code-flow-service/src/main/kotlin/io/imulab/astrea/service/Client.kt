package io.imulab.astrea.service

import io.imulab.astrea.sdk.flow.CodeRequest
import io.imulab.astrea.sdk.flow.TokenRequest
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.client.NotImplementedOidcClient
import io.imulab.astrea.sdk.oidc.client.OidcClient
import io.imulab.astrea.sdk.oidc.reserved.JweContentEncodingAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.JweKeyManagementAlgorithm

internal val notImplemented: () -> Nothing = { throw NotImplementedError("Not implemented. Should not be called.") }

/**
 * Adapter implementation of [OidcClient] that uses Grpc request payload as client data source. This client can only
 * be used during the authorization leg during the authorize code flow. Properties not included in the request are
 * regarded as 'not needed', hence throws [NotImplementedError] on invocation.
 */
class CodeFlowAuthorizeLegClient(source: CodeRequest.Client) : NotImplementedOidcClient() {
    override val id: String = source.id
    override val redirectUris: Set<String> = source.redirectUrisList.toSet()
    override val responseTypes: Set<String> = source.responseTypesList.toSet()
    override val scopes: Set<String> = source.scopesList.toSet()
}

/**
 * Adapter implementation of [OidcClient] that uses Grpc request payload as client data source. This client can only
 * be used during the token leg during the authorize code flow. Properties not included in the request are
 * regarded as 'not needed', hence throws [NotImplementedError] on invocation.
 */
class ClientFlowTokenLegClient(source: TokenRequest.Client) : NotImplementedOidcClient() {

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
    override val jwksUri: String = ""
}

/**
 * A client whose only fields populated is its id field. This implemented is used to revive request from storage.
 */
class IdOnlyClient(override val id: String) : NotImplementedOidcClient()