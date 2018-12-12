package io.imulab.astrea.service.client.support

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.node.ArrayNode
import io.imulab.astrea.sdk.client.Client

object ClientDbJsonSupport {

    // field names
    private object Field {
        const val id = "_id"
        const val clientName = "name"
        const val clientSecret = "secret"
        const val clientType = "type"
        const val redirectUris = "redirect_uris"
        const val responseTypes = "response_types"
        const val grantTypes = "grant_types"
        const val scopes = "scopes"
        const val applicationType = "app_ype"
        const val contacts = "contacts"
        const val logoUri = "logo_uri"
        const val clientUri = "client_uri"
        const val policyUri = "policy_uri"
        const val tosUri = "tos_uri"
        const val jwksUri = "jwks_uri"
        const val jwks = "jwks"
        const val sectorIdentifierUri = "sec_id_uri"
        const val subjectType = "subj_type"
        const val idTokenSignedResponseAlg = "id_tok_sig_alg"
        const val idTokenEncryptedResponseAlg = "id_tok_encrypt_alg"
        const val idTokenEncryptedResponseEnc = "id_tok_encrypt_enc"
        const val requestObjectSigningAlg = "req_obj_sig_alg"
        const val requestObjectEncryptionAlg = "req_obj_encrypt_alg"
        const val requestObjectEncryptionEnc = "req_obj_encrypt_enc"
        const val userinfoSignedResponseAlg = "uinfo_sig_alg"
        const val userinfoEncryptedResponseAlg = "uinfo_encrypt_alg"
        const val userinfoEncryptedResponseEnc = "uinfo_encrypt_enc"
        const val tokenEndpointAuthMethod = "tok_auth"
        const val defaultMaxAge = "max_age"
        const val requireAuthTime = "req_auth"
        const val defaultAcrValues = "acr"
        const val initiateLoginUri = "init_login_uri"
        const val requestUris = "req_uris"
        const val requests = "req"
    }

    val deserializer = object : JsonDeserializer<Client>() {
        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Client {
            val c = Client()
            if (p != null) {
                p.codec.readTree<JsonNode>(p).run {
                    setStr(Field.id) { c.id = it }
                    setStr(Field.clientName) { c.clientName = it }
                    setStr(Field.clientSecret) { c.clientSecret = it }
                    setStr(Field.clientType) { c.clientType = it }
                    setArray(Field.redirectUris) { c.redirectUris.add(it) }
                    setArray(Field.responseTypes) { c.responseTypes.add(it) }
                    setArray(Field.grantTypes) { c.grantTypes.add(it) }
                    setArray(Field.scopes) { c.scopes.add(it) }
                    setStr(Field.applicationType) { c.applicationType = it }
                    setArray(Field.contacts) { c.contacts.add(it) }
                    setStr(Field.logoUri) { c.logoUri = it }
                    setStr(Field.clientUri) { c.clientUri = it }
                    setStr(Field.policyUri) { c.policyUri = it }
                    setStr(Field.tosUri) { c.tosUri = it }
                    setStr(Field.jwksUri) { c.jwksUri = it }
                    setStr(Field.jwks) { c.jwks = it }
                    setStr(Field.sectorIdentifierUri) { c.sectorIdentifierUri = it }
                    setStr(Field.subjectType) { c.subjectType = it }
                    setStr(Field.idTokenSignedResponseAlg) { c.idTokenSignedResponseAlg = it }
                    setStr(Field.idTokenEncryptedResponseAlg) { c.idTokenEncryptedResponseAlg = it }
                    setStr(Field.idTokenEncryptedResponseEnc) { c.idTokenEncryptedResponseEnc = it }
                    setStr(Field.requestObjectSigningAlg) { c.requestObjectSigningAlg = it }
                    setStr(Field.requestObjectEncryptionAlg) { c.requestObjectEncryptionAlg = it }
                    setStr(Field.requestObjectEncryptionEnc) { c.requestObjectEncryptionEnc = it }
                    setStr(Field.userinfoSignedResponseAlg) { c.userinfoSignedResponseAlg = it }
                    setStr(Field.userinfoEncryptedResponseAlg) { c.userinfoEncryptedResponseAlg = it }
                    setStr(Field.userinfoEncryptedResponseEnc) { c.userinfoEncryptedResponseEnc = it }
                    setStr(Field.tokenEndpointAuthMethod) { c.tokenEndpointAuthMethod = it }
                    setLong(Field.defaultMaxAge) { c.defaultMaxAge = it }
                    setBoolean(Field.requireAuthTime) { c.requireAuthTime = it }
                    setArray(Field.defaultAcrValues) { c.defaultAcrValues.add(it) }
                    setStr(Field.initiateLoginUri) { c.initiateLoginUri = it }
                    setArray(Field.requestUris) { c.requestUris.add(it) }
                    setStringMap(Field.requests) { k, v -> c.requests[k] = v }
                }
            }
            return c
        }
    }

    val serializer = object : JsonSerializer<Client>() {
        override fun serialize(value: Client?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            checkNotNull(gen)

            gen.run {
                writeStartObject()
                if (value != null) {
                    str(Field.id, value.id)
                    str(Field.clientName, value.clientName)
                    str(Field.clientSecret, value.clientSecret)
                    str(Field.clientType, value.type)
                    array(Field.redirectUris, value.redirectUris)
                    array(Field.responseTypes, value.responseTypes)
                    array(Field.grantTypes, value.grantTypes)
                    array(Field.scopes, value.scopes)
                    str(Field.applicationType, value.applicationType)
                    array(Field.contacts, value.contacts)
                    str(Field.logoUri, value.logoUri)
                    str(Field.clientUri, value.clientUri)
                    str(Field.policyUri, value.policyUri)
                    str(Field.tosUri, value.tosUri)
                    str(Field.jwksUri, value.jwksUri)
                    str(Field.jwks, value.jwks)
                    str(Field.sectorIdentifierUri, value.sectorIdentifierUri)
                    str(Field.subjectType, value.subjectType)
                    str(Field.idTokenSignedResponseAlg, value.idTokenSignedResponseAlg)
                    str(Field.idTokenEncryptedResponseAlg, value.idTokenEncryptedResponseAlg)
                    str(Field.idTokenEncryptedResponseEnc, value.idTokenEncryptedResponseEnc)
                    str(Field.requestObjectSigningAlg, value.requestObjectSigningAlg)
                    str(Field.requestObjectEncryptionAlg, value.requestObjectEncryptionAlg)
                    str(Field.requestObjectEncryptionEnc, value.requestObjectEncryptionEnc)
                    str(Field.userinfoSignedResponseAlg, value.userinfoSignedResponseAlg)
                    str(Field.userinfoEncryptedResponseAlg, value.userinfoEncryptedResponseAlg)
                    str(Field.userinfoEncryptedResponseEnc, value.userinfoEncryptedResponseEnc)
                    str(Field.tokenEndpointAuthMethod, value.tokenEndpointAuthMethod)
                    gen.writeNumberField(Field.defaultMaxAge, value.defaultMaxAge)
                    gen.writeBooleanField(Field.requireAuthTime, value.requireAuthTime)
                    array(Field.defaultAcrValues, value.defaultAcrValues)
                    str(Field.initiateLoginUri, value.initiateLoginUri)
                    array(Field.requestUris, value.requestUris)
                    if (value.requests.isNotEmpty())
                        gen.writeObjectField(Field.requests, value.requests)
                }
                writeEndObject()
            }
        }
    }

    private fun JsonGenerator.str(name: String, value: String) {
        if (value.isNotEmpty())
            writeStringField(name, value)
    }

    private fun JsonGenerator.array(name: String, value: Collection<String>) {
        if (value.isNotEmpty()) {
            writeArrayFieldStart(name)
            value.forEach { v -> writeString(v) }
            writeEndArray()
        }
    }

    private fun JsonNode.setStr(name: String, action: (String) -> Unit) {
        if (hasNonNull(name) && get(name).isTextual)
            action(get(name).asText())
    }

    private fun JsonNode.setArray(name: String, action: (String) -> Unit) {
        if (hasNonNull(name) && get(name).isArray)
            (get(name) as ArrayNode).forEach {
                if (it.isTextual)
                    action(it.asText())
            }
    }

    private fun JsonNode.setLong(name: String, action: (Long) -> Unit) {
        if (hasNonNull(name) && get(name).isLong)
            action(get(name).asLong())
    }

    private fun JsonNode.setBoolean(name: String, action: (Boolean) -> Unit) {
        if (hasNonNull(name) && get(name).isBoolean)
            action(get(name).asBoolean())
    }

    private fun JsonNode.setStringMap(name: String, action: (String, String) -> Unit) {
        if (hasNonNull(name) && get(name).isObject)
            get(name).fields().forEach { entry ->
                action(entry.key, entry.value.asText())
            }
    }
}