package io.imulab.astrea.sdk.oidc.request

import io.imulab.astrea.sdk.oidc.`when`
import io.imulab.astrea.sdk.oidc.given
import io.imulab.astrea.sdk.oidc.then
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.spekframework.spek2.Spek
import java.time.LocalDateTime

object OidcSessionSpec : Spek({

    given("Two oidc sessions") {
        val s1 = OidcSession(
            subject = "",
            obfuscatedSubject = "",
            acrValues = mutableListOf(),
            idTokenClaims = mutableMapOf("a" to "1")
        )
        val s2 = OidcSession(
            subject = "foo",
            obfuscatedSubject = "oof",
            authTime = LocalDateTime.now(),
            nonce = "12345678",
            acrValues = mutableListOf("gold"),
            idTokenClaims = mutableMapOf("b" to "1")
        )

        `when`("Merged") {
            s1.merge(s2)

            then("The merger should contain subject") {
                assertThat(s1.subject).isEqualTo("foo")
            }

            then("The merger should contain obs subject") {
                assertThat(s1.obfuscatedSubject).isEqualTo("oof")
            }

            then("The merger should contain auth time") {
                assertThat(s1.authTime).isNotNull()
            }

            then("The merger should contain nonce") {
                assertThat(s1.nonce).isEqualTo("12345678")
            }

            then("The merger should contain all acr values") {
                assertThat(s1.acrValues).contains("gold")
            }

            then("The merger should contain all id token claims") {
                assertThat(s1.idTokenClaims).containsKeys("a", "b")
            }
        }
    }
})