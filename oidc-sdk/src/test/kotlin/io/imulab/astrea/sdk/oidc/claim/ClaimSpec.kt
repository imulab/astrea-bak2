package io.imulab.astrea.sdk.oidc.claim

import io.imulab.astrea.sdk.oidc.`when`
import io.imulab.astrea.sdk.oidc.given
import io.imulab.astrea.sdk.oidc.then
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek

object ClaimSpec : Spek({

    given("An example claim") {
        val claim = Claim(name = "foo", essential = true, source = "id_token", values = listOf("bar"))

        then("name is foo") {
            assertThat(claim.name).isEqualTo("foo")
        }

        then("claim is essential") {
            assertThat(claim.essential).isTrue()
        }

        then("source is id_token") {
            assertThat(claim.source).isEqualTo("id_token")
        }

        then("values contain bar") {
            assertThat(claim.values).contains("bar").hasSize(1)
        }

        `when`("invoking the map representation") {
            val map = claim.toMap()

            then("map contains all the above entries") {
                assertThat(map).containsKeys("name", "essential", "source", "values")
            }
        }
    }
})