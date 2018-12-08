package io.imulab.astrea.sdk.client

import io.grpc.Channel
import io.grpc.Server
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.imulab.astrea.sdk.oauth.client.OAuthClient
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

object GrpcClientLookupSpec : Spek({

    given("""
        A ClientLookup service implementation, and
        a local client stub
    """.trimIndent()) {
        val server: Server = InProcessServerBuilder
            .forName(DummyClientLookupService.serviceName)
            .directExecutor()
            .addService(DummyClientLookupService)
            .build().also {
                Runtime.getRuntime().addShutdownHook(thread(start = false) {
                    it.shutdown()
                    it.awaitTermination(5, TimeUnit.SECONDS)
                })
            }
            .start()
        val channel: Channel = InProcessChannelBuilder
            .forName(DummyClientLookupService.serviceName)
            .directExecutor()
            .build()
        val service = GrpcClientLookup(channel)

        `when`("A client lookup request is initiated") {
            val result = runCatching {
                runBlocking { service.find("foo") }
            }
            var client: OAuthClient? = null

            then("should have returned a client") {
                assertThat(result.isSuccess).isTrue()
                client = result.getOrThrow()
            }

            then("the client should be identical to the server side") {
                assertThat(client).isNotNull
                assertThat(client).isInstanceOf(Client::class.java)
                (client as Client).run {
                    val p = DummyClientLookupService.prototype
                    assertThat(id).isEqualTo("foo")
                    assertThat(name).isEqualTo(p.name)
                    assertThat(type).isEqualTo(p.type)
                    assertThat(redirectUris).containsAll(p.redirectUrisList)
                    assertThat(responseTypes).containsAll(p.responseTypesList)
                    assertThat(grantTypes).containsAll(p.grantTypesList)
                    assertThat(scopes).containsAll(p.scopesList)
                    assertThat(applicationType).isEqualTo(p.applicationType)
                    assertThat(contacts).containsAll(p.contactsList)
                    assertThat(logoUri).isEqualTo(p.logoUri)
                    assertThat(clientUri).isEqualTo(p.clientUri)
                    assertThat(policyUri).isEqualTo(p.policyUri)
                    assertThat(tosUri).isEqualTo(p.tosUri)
                    assertThat(jwksUri).isEqualTo(p.jwksUri)
                    assertThat(sectorIdentifierUri).isEqualTo(p.sectorIdentifierUri)
                    assertThat(subjectType).isEqualTo(p.subjectType)
                    assertThat(idTokenSignedResponseAlgorithm.name).isEqualTo(p.idTokenSignedResponseAlgorithm)
                    assertThat(idTokenEncryptedResponseAlgorithm.name).isEqualTo(p.idTokenEncryptedResponseAlgorithm)
                    assertThat(idTokenEncryptedResponseEncoding.name).isEqualTo(p.idTokenEncryptedResponseEncoding)
                    assertThat(requestObjectSigningAlgorithm.name).isEqualTo(p.requestObjectSigningAlgorithm)
                    assertThat(requestObjectEncryptionAlgorithm.name).isEqualTo(p.requestObjectEncryptionAlgorithm)
                    assertThat(requestObjectEncryptionEncoding.name).isEqualTo(p.requestObjectEncryptionEncoding)
                    assertThat(userInfoSignedResponseAlgorithm.name).isEqualTo(p.userInfoSignedResponseAlgorithm)
                    assertThat(userInfoEncryptedResponseAlgorithm.name).isEqualTo(p.userInfoEncryptedResponseAlgorithm)
                    assertThat(userInfoEncryptedResponseEncoding.name).isEqualTo(p.userInfoEncryptedResponseEncoding)
                    assertThat(tokenEndpointAuthenticationMethod).isEqualTo(p.tokenEndpointAuthenticationMethod)
                    assertThat(defaultMaxAge).isEqualTo(p.defaultMaxAge)
                    assertThat(requireAuthTime).isEqualTo(p.requireAuthTime)
                    assertThat(defaultAcrValues).containsAll(p.defaultAcrValuesList)
                    assertThat(initiateLoginUri).isEqualTo(p.initiateLoginUri)
                    assertThat(requestUris).containsAll(p.requestUrisList)
                }
            }
        }

        after {
            runBlocking {
                server.shutdown()
                server.awaitTermination(5, TimeUnit.SECONDS)
            }
        }
    }
})