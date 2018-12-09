package io.imulab.astrea.service.client

import com.fasterxml.jackson.annotation.JsonProperty
import io.imulab.astrea.sdk.client.Client
import io.imulab.astrea.sdk.oauth.reserved.AuthenticationMethod
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.JweContentEncodingAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.JweKeyManagementAlgorithm
import java.util.LinkedHashSet

class ClientDTO(
    @JsonProperty("client_name")
    name: String = "",

    @JsonProperty("client_type")
    type: String = "",

    @JsonProperty("redirect_uris")
    redirectUris: Set<String> = emptySet(),

    @JsonProperty("response_types")
    responseTypes: Set<String> = emptySet(),

    @JsonProperty("grant_types")
    grantTypes: Set<String> = emptySet(),

    @JsonProperty("scopes")
    scopes: Set<String> = emptySet(),

    @JsonProperty("application_type")
    applicationType: String = "",

    @JsonProperty("contacts")
    contacts: LinkedHashSet<String> = LinkedHashSet(),

    @JsonProperty("logo_uri")
    logoUri: String = "",

    @JsonProperty("client_uri")
    clientUri: String = "",

    @JsonProperty("policy_uri")
    policyUri: String = "",

    @JsonProperty("tos_uri")
    tosUri: String = "",

    @JsonProperty("jwks_uri")
    jwksUri: String = "",

    @JsonProperty("jwks")
    jwks: String = "",

    @JsonProperty("sector_identifier_uri")
    sectorIdentifierUri: String = "",

    @JsonProperty("subject_type")
    subjectType: String = "",

    @JsonProperty("id_token_signed_response_alg")
    idTokenSignedResponseAlgorithm: String = JwtSigningAlgorithm.RS256.spec,

    @JsonProperty("id_token_encrypted_response_alg")
    idTokenEncryptedResponseAlgorithm: String = JweKeyManagementAlgorithm.None.spec,

    @JsonProperty("id_token_encrypted_response_enc")
    idTokenEncryptedResponseEncoding: String = JweContentEncodingAlgorithm.None.spec,

    @JsonProperty("request_object_signing_alg")
    requestObjectSigningAlgorithm: String = JwtSigningAlgorithm.RS256.spec,

    @JsonProperty("request_object_encryption_alg")
    requestObjectEncryptionAlgorithm: String = JweKeyManagementAlgorithm.None.spec,

    @JsonProperty("request_object_encryption_enc")
    requestObjectEncryptionEncoding: String = JweContentEncodingAlgorithm.None.spec,

    @JsonProperty("userinfo_signed_response_alg")
    userInfoSignedResponseAlgorithm: String = JwtSigningAlgorithm.RS256.spec,

    @JsonProperty("userinfo_encrypted_response_alg")
    userInfoEncryptedResponseAlgorithm: String = JweKeyManagementAlgorithm.None.spec,

    @JsonProperty("userinfo_encrypted_response_enc")
    userInfoEncryptedResponseEncoding: String = JweContentEncodingAlgorithm.None.spec,

    @JsonProperty("token_endpoint_auth_method")
    tokenEndpointAuthenticationMethod: String = AuthenticationMethod.clientSecretPost,

    @JsonProperty("token_endpoint_auth_signing_alg")    // todo
    tokenEndpointAuthenticationSigningAlgorithm: String = JwtSigningAlgorithm.RS256.spec,

    @JsonProperty("default_max_age")
    defaultMaxAge: Long = 0,

    @JsonProperty("require_auth_time")
    requireAuthTime: Boolean = false,

    @JsonProperty("default_acr_values")
    defaultAcrValues: List<String> = emptyList(),

    @JsonProperty("initiate_login_uri")
    initiateLoginUri: String = "",

    @JsonProperty("request_uris")
    requestUris: List<String> = emptyList()
) : Client(
    id = "",
    secret = ByteArray(0),
    name = name,
    type = type,
    redirectUris = redirectUris,
    responseTypes = responseTypes,
    grantTypes = grantTypes,
    scopes = scopes,
    applicationType = applicationType,
    contacts = contacts,
    logoUri = logoUri,
    clientUri = clientUri,
    policyUri = policyUri,
    tosUri = tosUri,
    jwksUri = jwksUri,
    jwks = jwks,
    sectorIdentifierUri = sectorIdentifierUri,
    subjectType = subjectType,
    idTokenSignedResponseAlgorithm = JwtSigningAlgorithm.fromSpec(idTokenSignedResponseAlgorithm),
    idTokenEncryptedResponseAlgorithm = JweKeyManagementAlgorithm.fromSpec(idTokenEncryptedResponseAlgorithm),
    idTokenEncryptedResponseEncoding = JweContentEncodingAlgorithm.fromSpec(idTokenEncryptedResponseEncoding),
    requestObjectSigningAlgorithm = JwtSigningAlgorithm.fromSpec(requestObjectSigningAlgorithm),
    requestObjectEncryptionAlgorithm = JweKeyManagementAlgorithm.fromSpec(requestObjectEncryptionAlgorithm),
    requestObjectEncryptionEncoding = JweContentEncodingAlgorithm.fromSpec(requestObjectEncryptionEncoding),
    userInfoSignedResponseAlgorithm = JwtSigningAlgorithm.fromSpec(userInfoSignedResponseAlgorithm),
    userInfoEncryptedResponseAlgorithm = JweKeyManagementAlgorithm.fromSpec(userInfoEncryptedResponseAlgorithm),
    userInfoEncryptedResponseEncoding = JweContentEncodingAlgorithm.fromSpec(userInfoEncryptedResponseEncoding),
    tokenEndpointAuthenticationMethod = tokenEndpointAuthenticationMethod,
    defaultMaxAge = defaultMaxAge,
    requireAuthTime = requireAuthTime,
    defaultAcrValues = defaultAcrValues,
    initiateLoginUri = initiateLoginUri,
    requestUris = requestUris
)