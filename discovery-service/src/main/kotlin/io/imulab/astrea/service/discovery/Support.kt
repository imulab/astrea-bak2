package io.imulab.astrea.service.discovery

import com.fasterxml.jackson.annotation.JsonProperty

abstract class DiscoveryMixin(
    @JsonProperty("issuer")
    var issuer: String = "",
    @JsonProperty("authorization_endpoint")
    var authorizationEndpoint: String = "",
    @JsonProperty("token_endpoint")
    var tokenEndpoint: String = "",
    @JsonProperty("userinfo_endpoint")
    var userInfoEndpoint: String = "",
    @JsonProperty("jwks_uri")
    var jwksUri: String = "",
    @JsonProperty("registration_endpoint")
    var registrationEndpoint: String = "",
    @JsonProperty("scopes_supported")
    var scopesSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("response_types_supported")
    var responseTypesSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("response_modes_supported")
    var responseModeSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("grant_types_supported")
    var grantTypesSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("acr_values_supported")
    var acrValuesSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("subject_types_supported")
    var subjectTypesSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("id_token_signing_alg_values_supported")
    var idTokenSigningAlgorithmValuesSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("id_token_encryption_alg_values_supported")
    var idTokenEncryptionAlgorithmValuesSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("id_token_encryption_enc_values_supported")
    var idTokenEncryptionEncodingValuesSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("userinfo_signing_alg_values_supported")
    var userInfoSigningAlgorithmValuesSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("userinfo_encryption_alg_values_supported")
    var userInfoEncryptionAlgorithmValuesSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("userinfo_encryption_enc_values_supported")
    var userInfoEncryptionEncodingValuesSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("request_object_signing_alg_values_supported")
    var requestObjectSigningAlgorithmValuesSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("request_object_encryption_alg_values_supported")
    var requestObjectEncryptionAlgorithmValuesSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("request_object_encryption_enc_values_supported")
    var requestObjectEncryptionEncodingValuesSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("token_endpoint_auth_methods_supported")
    var tokenEndpointAuthenticationMethodsSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("token_endpoint_auth_signing_alg_values_supported")
    var tokenEndpointAuthenticationSigningAlgorithmValuesSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("display_values_supported")
    var displayValuesSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("claim_types_supported")
    var claimTypesSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("claims_supported")
    var claimsSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("service_documentation")
    var serviceDocumentation: String = "",
    @JsonProperty("claims_locales_supported")
    var claimsLocalesSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("ui_locales_supported")
    var uiLocalesSupported: MutableList<String> = mutableListOf(),
    @JsonProperty("claims_parameter_supported")
    var claimsParameterSupported: Boolean = false,
    @JsonProperty("request_parameter_supported")
    var requestParameterSupported: Boolean = false,
    @JsonProperty("request_uri_parameter_supported")
    var requestUriParameterSupported: Boolean = false,
    @JsonProperty("require_request_uri_registration")
    var requireRequestUriRegistration: Boolean = false,
    @JsonProperty("op_policy_uri")
    var opPolicyUri: String = "",
    @JsonProperty("op_tos_uri")
    var opTosUri: String = ""
)