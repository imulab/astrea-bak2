package io.imulab.astrea.service.client.rest

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.imulab.astrea.sdk.client.Client
import io.imulab.astrea.sdk.oauth.reserved.AuthenticationMethod
import io.imulab.astrea.sdk.oauth.reserved.ClientType
import io.imulab.astrea.sdk.oauth.reserved.GrantType
import io.imulab.astrea.sdk.oauth.reserved.ResponseType
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.ApplicationType
import io.imulab.astrea.sdk.oidc.reserved.JweContentEncodingAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.JweKeyManagementAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.SubjectType

class ClientDTO(
    @JsonIgnore
    var id: String = "",

    @JsonProperty("client_name")
    var name: String = "",

    @JsonIgnore
    var secret: String = "",

    @JsonProperty("client_type")
    var type: String = "",

    @JsonProperty("redirect_uris")
    var redirectUris: Set<String> = emptySet(),

    @JsonProperty("response_types")
    var responseTypes: Set<String> = emptySet(),

    @JsonProperty("grant_types")
    var grantTypes: Set<String> = emptySet(),

    @JsonProperty("scopes")
    var scopes: MutableSet<String> = mutableSetOf(),

    @JsonProperty("application_type")
    var applicationType: String = "",

    @JsonProperty("contacts")
    var contacts: LinkedHashSet<String> = LinkedHashSet(),

    @JsonProperty("logo_uri")
    var logoUri: String = "",

    @JsonProperty("client_uri")
    var clientUri: String = "",

    @JsonProperty("policy_uri")
    var policyUri: String = "",

    @JsonProperty("tos_uri")
    var tosUri: String = "",

    @JsonProperty("jwks_uri")
    var jwksUri: String = "",

    @JsonProperty("jwks")
    var jwks: String = "",

    @JsonProperty("sector_identifier_uri")
    var sectorIdentifierUri: String = "",

    @JsonProperty("subject_type")
    var subjectType: String = "",

    @JsonProperty("id_token_signed_response_alg")
    var idTokenSignedResponseAlg: String = JwtSigningAlgorithm.RS256.spec,

    @JsonProperty("id_token_encrypted_response_alg")
    var idTokenEncryptedResponseAlg: String = JweKeyManagementAlgorithm.None.spec,

    @JsonProperty("id_token_encrypted_response_enc")
    var idTokenEncryptedResponseEnc: String = JweContentEncodingAlgorithm.None.spec,

    @JsonProperty("request_object_signing_alg")
    var requestObjectSigningAlg: String = JwtSigningAlgorithm.RS256.spec,

    @JsonProperty("request_object_encryption_alg")
    var requestObjectEncryptionAlg: String = JweKeyManagementAlgorithm.None.spec,

    @JsonProperty("request_object_encryption_enc")
    var requestObjectEncryptionEnc: String = JweContentEncodingAlgorithm.None.spec,

    @JsonProperty("userinfo_signed_response_alg")
    var userInfoSignedResponseAlg: String = JwtSigningAlgorithm.RS256.spec,

    @JsonProperty("userinfo_encrypted_response_alg")
    var userInfoEncryptedResponseAlg: String = JweKeyManagementAlgorithm.None.spec,

    @JsonProperty("userinfo_encrypted_response_enc")
    var userInfoEncryptedResponseEnc: String = JweContentEncodingAlgorithm.None.spec,

    @JsonProperty("token_endpoint_auth_method")
    var tokenEndpointAuthenticationMethod: String = AuthenticationMethod.clientSecretPost,

    @JsonProperty("token_endpoint_auth_signing_alg")    // todo
    var tokenEndpointAuthenticationSigningAlg: String = JwtSigningAlgorithm.RS256.spec,

    @JsonProperty("default_max_age")
    var defaultMaxAge: Long = 0,

    @JsonProperty("require_auth_time")
    var requireAuthTime: Boolean = false,

    @JsonProperty("default_acr_values")
    var defaultAcrValues: List<String> = emptyList(),

    @JsonProperty("initiate_login_uri")
    var initiateLoginUri: String = "",

    @JsonProperty("request_uris")
    var requestUris: List<String> = emptyList()
) {

    companion object {
        val defaultPrototype = ClientDTO(
            id = "",
            name = "",
            secret = "",
            type = ClientType.confidential,
            redirectUris = emptySet(),
            responseTypes = setOf(ResponseType.code),
            grantTypes = setOf(GrantType.authorizationCode),
            scopes = mutableSetOf(),
            applicationType = ApplicationType.web,
            contacts = LinkedHashSet(),
            logoUri = "",
            clientUri = "",
            policyUri = "",
            tosUri = "",
            jwksUri = "",
            jwks = "",
            sectorIdentifierUri = "",
            subjectType = SubjectType.public,
            idTokenSignedResponseAlg = JwtSigningAlgorithm.RS256.spec,
            idTokenEncryptedResponseAlg = JweKeyManagementAlgorithm.None.spec,
            idTokenEncryptedResponseEnc = JweContentEncodingAlgorithm.None.spec,
            requestObjectSigningAlg = JwtSigningAlgorithm.RS256.spec,
            requestObjectEncryptionAlg = JweKeyManagementAlgorithm.None.spec,
            requestObjectEncryptionEnc = JweContentEncodingAlgorithm.None.spec,
            userInfoSignedResponseAlg = JwtSigningAlgorithm.RS256.spec,
            userInfoEncryptedResponseAlg = JweKeyManagementAlgorithm.None.spec,
            userInfoEncryptedResponseEnc = JweContentEncodingAlgorithm.None.spec,
            tokenEndpointAuthenticationMethod = AuthenticationMethod.clientSecretBasic,
            tokenEndpointAuthenticationSigningAlg = JwtSigningAlgorithm.RS256.spec,
            defaultMaxAge = 0,
            requireAuthTime = false,
            defaultAcrValues = emptyList(),
            initiateLoginUri = "",
            requestUris = emptyList()
        )
    }

    fun merge(another: ClientDTO) {
        if (id.isEmpty()) {
            id = another.id
        }
        if (name.isEmpty()) {
            name = another.name
        }
        if (secret.isEmpty()) {
            secret = another.secret
        }
        if (type.isEmpty()) {
            type = another.type
        }
        if (redirectUris.isEmpty()) {
            redirectUris = another.redirectUris
        }
        if (responseTypes.isEmpty()) {
            responseTypes = another.responseTypes
        }
        if (grantTypes.isEmpty()) {
            grantTypes = another.grantTypes
        }
        if (scopes.isEmpty()) {
            scopes = another.scopes
        }
        if (applicationType.isEmpty()) {
            applicationType = another.applicationType
        }
        if (contacts.isEmpty()) {
            contacts = another.contacts
        }
        if (logoUri.isEmpty()) {
            logoUri = another.logoUri
        }
        if (clientUri.isEmpty()) {
            clientUri = another.clientUri
        }
        if (policyUri.isEmpty()) {
            policyUri = another.policyUri
        }
        if (tosUri.isEmpty()) {
            tosUri = another.tosUri
        }
        if (jwksUri.isEmpty()) {
            jwksUri = another.jwksUri
        }
        if (jwks.isEmpty()) {
            jwks = another.jwks
        }
        if (sectorIdentifierUri.isEmpty()) {
            sectorIdentifierUri = another.sectorIdentifierUri
        }
        if (subjectType.isEmpty()) {
            subjectType = another.subjectType
        }
        if (idTokenSignedResponseAlg.isEmpty()) {
            idTokenSignedResponseAlg = another.idTokenSignedResponseAlg
        }
        if (idTokenEncryptedResponseAlg.isEmpty()) {
            idTokenEncryptedResponseAlg = another.idTokenEncryptedResponseAlg
        }
        if (idTokenEncryptedResponseEnc.isEmpty()) {
            idTokenEncryptedResponseEnc = another.idTokenEncryptedResponseEnc
        }
        if (requestObjectSigningAlg.isEmpty()) {
            requestObjectSigningAlg = another.requestObjectSigningAlg
        }
        if (requestObjectEncryptionAlg.isEmpty()) {
            requestObjectEncryptionAlg = another.requestObjectEncryptionAlg
        }
        if (requestObjectEncryptionEnc.isEmpty()) {
            requestObjectEncryptionEnc = another.requestObjectEncryptionEnc
        }
        if (userInfoSignedResponseAlg.isEmpty()) {
            userInfoSignedResponseAlg = another.userInfoSignedResponseAlg
        }
        if (userInfoEncryptedResponseAlg.isEmpty()) {
            userInfoEncryptedResponseAlg = another.userInfoEncryptedResponseAlg
        }
        if (userInfoEncryptedResponseEnc.isEmpty()) {
            userInfoEncryptedResponseEnc = another.userInfoEncryptedResponseEnc
        }
        if (tokenEndpointAuthenticationMethod.isEmpty()) {
            tokenEndpointAuthenticationMethod = another.tokenEndpointAuthenticationMethod
        }
        if (tokenEndpointAuthenticationSigningAlg.isEmpty()) {
            tokenEndpointAuthenticationSigningAlg = another.tokenEndpointAuthenticationSigningAlg
        }
        if (defaultMaxAge == 0L) {
            defaultMaxAge = another.defaultMaxAge
        }
        if (!requireAuthTime) {
            requireAuthTime = another.requireAuthTime
        }
        if (defaultAcrValues.isEmpty()) {
            defaultAcrValues = another.defaultAcrValues
        }
        if (initiateLoginUri.isEmpty()) {
            initiateLoginUri = another.initiateLoginUri
        }
        if (requestUris.isEmpty()) {
            requestUris = another.requestUris
        }
    }

    fun buildClient(): Client {
        return Client(
            id = id,
            clientName = name,
            clientSecret = secret,
            clientType = type,
            redirectUris = redirectUris.toMutableSet(),
            responseTypes = responseTypes.toMutableSet(),
            grantTypes = grantTypes.toMutableSet(),
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
            idTokenSignedResponseAlg = idTokenSignedResponseAlg,
            idTokenEncryptedResponseAlg = idTokenEncryptedResponseAlg,
            idTokenEncryptedResponseEnc = idTokenEncryptedResponseEnc,
            requestObjectSigningAlg = requestObjectSigningAlg,
            requestObjectEncryptionAlg = requestObjectEncryptionAlg,
            requestObjectEncryptionEnc = requestObjectEncryptionEnc,
            userinfoSignedResponseAlg = userInfoSignedResponseAlg,
            userinfoEncryptedResponseAlg = userInfoEncryptedResponseAlg,
            userinfoEncryptedResponseEnc = userInfoEncryptedResponseEnc,
            tokenEndpointAuthMethod = tokenEndpointAuthenticationMethod,
            defaultMaxAge = defaultMaxAge,
            requireAuthTime = requireAuthTime,
            defaultAcrValues = defaultAcrValues.toMutableList(),
            initiateLoginUri = initiateLoginUri,
            requestUris = requestUris.toMutableList()
        )
    }
}