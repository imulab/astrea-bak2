package io.imulab.astrea.service.client.common

import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oauth.validation.ClientTypeValidator
import io.imulab.astrea.sdk.oauth.validation.OAuthGrantTypeValidator
import io.imulab.astrea.sdk.oauth.validation.ScopeValidator
import io.imulab.astrea.sdk.oidc.reserved.JweContentEncodingAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.JweKeyManagementAlgorithm
import io.imulab.astrea.sdk.oidc.validation.OidcResponseTypeValidator
import io.imulab.astrea.sdk.oidc.validation.SubjectTypeValidator
import io.imulab.astrea.service.client.rest.ClientDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator

@Component
class ClientDTOValidator : Validator {

    @Autowired
    lateinit var repository: ClientRepository

    override fun supports(clazz: Class<*>): Boolean = clazz == ClientDTO::class.java

    override fun validate(target: Any, errors: Errors) {
        check(target is ClientDTO)

        if (target.name.isEmpty())
            errors.reject("client_name.empty", "client_name is required.")
        else if (repository.countByName(target.name) > 0)
            errors.reject("client_name.exists", "client_name already exists.")

        if (!tryOrFalse { ClientTypeValidator.validate(target.type) })
            errors.reject("client_type.invalid", "client_type is invalid.")

        // TODO redirect uri spec validator

        if (!tryOrFalse { target.responseTypes.forEach { OidcResponseTypeValidator.validate(it) } })
            errors.reject("response_type.invalid", "response_type is invalid.")

        if (!tryOrFalse { target.grantTypes.forEach { OAuthGrantTypeValidator.validate(it) } })
            errors.reject("grant_type.invalid", "grant_type is invalid.")

        if (!tryOrFalse { target.scopes.forEach { ScopeValidator.validate(it) } })
            errors.reject("scope.invalid", "scope is invalid.")

        // TODO application type validator

        if (target.jwks.isNotEmpty() && target.jwksUri.isNotEmpty())
            errors.reject("jwks.both", "jwks_uri and jwks cannot be both supplied.")

        if (!tryOrFalse { SubjectTypeValidator.validate(target.subjectType) })
            errors.reject("subject_type.invalid", "subject_type is invalid.")

        if (!tryOrFalse { JwtSigningAlgorithm.fromSpec(target.idTokenSignedResponseAlg) })
            errors.reject("id_token_signed_response_alg.invalid", "id_token_signed_response_alg is invalid.")

        if (!tryOrFalse { JweKeyManagementAlgorithm.fromSpec(target.idTokenEncryptedResponseAlg) })
            errors.reject("id_token_encrypted_response_alg.invalid", "id_token_encrypted_response_alg is invalid.")

        if (!tryOrFalse { JweContentEncodingAlgorithm.fromSpec(target.idTokenEncryptedResponseEnc) })
            errors.reject("id_token_encrypted_response_enc.invalid", "id_token_encrypted_response_enc is invalid.")

        if (!tryOrFalse { JwtSigningAlgorithm.fromSpec(target.requestObjectSigningAlg) })
            errors.reject("request_object_signing_alg.invalid", "request_object_signing_alg is invalid.")

        if (!tryOrFalse { JweKeyManagementAlgorithm.fromSpec(target.requestObjectEncryptionAlg) })
            errors.reject("request_object_encryption_alg.invalid", "request_object_encryption_alg is invalid.")

        if (!tryOrFalse { JweContentEncodingAlgorithm.fromSpec(target.requestObjectEncryptionEnc) })
            errors.reject("request_object_encryption_enc.invalid", "request_object_encryption_enc is invalid.")

        if (!tryOrFalse { JwtSigningAlgorithm.fromSpec(target.userInfoSignedResponseAlg) })
            errors.reject("userinfo_signed_response_alg.invalid", "userinfo_signed_response_alg is invalid.")

        if (!tryOrFalse { JweKeyManagementAlgorithm.fromSpec(target.userInfoEncryptedResponseAlg) })
            errors.reject("userinfo_encrypted_response_alg.invalid", "userinfo_encrypted_response_alg is invalid.")

        if (!tryOrFalse { JweContentEncodingAlgorithm.fromSpec(target.userInfoEncryptedResponseEnc) })
            errors.reject("userinfo_encrypted_response_enc.invalid", "userinfo_encrypted_response_enc is invalid.")

        // todo authentication method validator

        if (!tryOrFalse { JwtSigningAlgorithm.fromSpec(target.tokenEndpointAuthenticationSigningAlg) })
            errors.reject("token_endpoint_auth_signing_alg.invalid", "token_endpoint_auth_signing_alg is invalid.")

        if (target.defaultMaxAge < 0)
            errors.reject("default_max_age.invalid", "default_max_age is invalid.")
    }

    private fun tryOrFalse(block: () -> Unit): Boolean {
        return try {
            block()
            true
        } catch (e: Exception) {
            false
        }
    }
}