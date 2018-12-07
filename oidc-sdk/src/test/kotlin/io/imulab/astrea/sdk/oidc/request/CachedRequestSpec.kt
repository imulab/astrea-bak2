package io.imulab.astrea.sdk.oidc.request

import io.imulab.astrea.sdk.oidc.given
import io.imulab.astrea.sdk.oidc.then
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import java.time.LocalDateTime

object CachedRequestSpec : Spek({

    given("A cached request with expiration in the future") {
        val request = CachedRequest(
            requestUri = "",
            request = "",
            expiry = LocalDateTime.now().plusDays(1)
        )

        then("it has not expired") {
            assertThat(request.hasExpired()).isFalse()
        }
    }

    given("A cached request with expiration in the past") {
        val request = CachedRequest(
            requestUri = "",
            request = "",
            expiry = LocalDateTime.now().minusSeconds(10)
        )

        then("it has expired") {
            assertThat(request.hasExpired()).isTrue()
        }
    }

    given("A cached request with no expiration") {
        val request = CachedRequest(
            requestUri = "",
            request = ""
        )

        then("it has not expired") {
            assertThat(request.hasExpired()).isFalse()
        }
    }
})