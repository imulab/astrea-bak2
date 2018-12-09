package io.imulab.astrea.service.client.common

import io.imulab.astrea.service.client.`when`
import io.imulab.astrea.service.client.given
import io.imulab.astrea.service.client.then
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek

object PasswordGeneratorSpec : Spek({

    given("A password generator") {
        val generator = PasswordGenerator

        `when`("Asked to generate a password") {
            val password = generator.generateAlphaNumericPassword(32)

            then("Should have generated a new password") {
                assertThat(password).isNotEmpty()
                assertThat(password).hasSize(32)
            }
        }
    }
})