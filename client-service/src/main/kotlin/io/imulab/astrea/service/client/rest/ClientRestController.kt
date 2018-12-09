package io.imulab.astrea.service.client.rest

import com.fasterxml.jackson.annotation.JsonProperty
import io.imulab.astrea.sdk.oauth.client.pwd.BCryptPasswordEncoder
import io.imulab.astrea.sdk.oauth.request.OAuthAccessRequest
import io.imulab.astrea.sdk.oauth.request.OAuthSession
import io.imulab.astrea.sdk.oauth.reserved.GrantType
import io.imulab.astrea.sdk.oauth.token.strategy.AccessTokenStrategy
import io.imulab.astrea.sdk.oidc.jwk.JsonWebKeySetStrategy
import io.imulab.astrea.service.client.clientScope
import io.imulab.astrea.service.client.common.ClientDTOValidator
import io.imulab.astrea.service.client.common.ClientRepository
import io.imulab.astrea.service.client.common.PasswordGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.validation.MapBindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.HashMap

@RestController
@RequestMapping("/client")
class ClientRestController {

    @Value("\${service.uriBase}")
    lateinit var baseUrl: String

    @Autowired
    lateinit var repository: ClientRepository
    @Autowired
    lateinit var validator: ClientDTOValidator
    @Autowired
    lateinit var jwksStrategy: JsonWebKeySetStrategy
    @Autowired
    lateinit var accessTokenStrategy: AccessTokenStrategy

    private val passwordEncoder = BCryptPasswordEncoder()

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createClient(@RequestBody request: ClientDTO): CreateClientResponse {
        request.merge(ClientDTO.defaultPrototype)
        MapBindingResult(HashMap<Any, Any>(), "client")
            .also { validator.validate(request, it) }
            .let {
                if (it.hasErrors())
                    throw RuntimeException(it.allErrors.joinToString(",") { e -> e.defaultMessage ?: "" })
            }

        runBlocking(Dispatchers.IO) { jwksStrategy.resolveKeySet(request.buildClient()) }
            .let { request.jwks = it.toJson() }

        val plainSecret = PasswordGenerator.generateAlphaNumericPassword(32)
        with(request) {
            id = UUID.randomUUID().toString()
            secret = passwordEncoder.encode(plainSecret)
            scopes.add(clientScope)
        }

        val client = repository.save(request.buildClient())

        val accessToken = runBlocking {
            accessTokenStrategy.generateToken(OAuthAccessRequest.Builder().also { b ->
                b.client = client
                b.grantTypes = mutableSetOf(GrantType.clientCredentials)
                b.redirectUri = "not-needed"
                b.session = OAuthSession(
                    subject = client.id,
                    grantedScopes = mutableSetOf(clientScope)
                )
            }.build())
        }

        return CreateClientResponse(
            id = client.id,
            secret = plainSecret,
            uri = "$baseUrl/${client.id}",
            accessToken = accessToken,
            iat = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
            expiry = 0
        )
    }

    data class CreateClientResponse(
        @JsonProperty("client_id")
        val id: String,
        @JsonProperty("client_secret")
        val secret: String,
        @JsonProperty("registration_access_token")
        val accessToken: String = "",
        @JsonProperty("registration_client_uri")
        val uri: String = "",
        @JsonProperty("client_id_issued_at")
        val iat: Long,
        @JsonProperty("client_secret_expires_at")
        val expiry: Long = 0
    )
}