package io.imulab.astrea.sdk.oidc.claim

import io.imulab.astrea.sdk.oidc.`when`
import io.imulab.astrea.sdk.oidc.given
import io.imulab.astrea.sdk.oidc.then
import org.assertj.core.api.Assertions.assertThat
import org.jose4j.jwk.RsaJwkGenerator
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.spekframework.spek2.Spek

object ClaimsSpec : Spek({

    given("an example token with claims from Open ID Connect Core 1.0 specification") {
        val token = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImsyYmRjIn0.ew0KICJpc3MiOiAiczZCaGRSa3F0MyIsDQogImF1ZCI6ICJodHRwczovL3NlcnZlci5leGFtcGxlLmNvbSIsDQogInJlc3BvbnNlX3R5cGUiOiAiY29kZSBpZF90b2tlbiIsDQogImNsaWVudF9pZCI6ICJzNkJoZFJrcXQzIiwNCiAicmVkaXJlY3RfdXJpIjogImh0dHBzOi8vY2xpZW50LmV4YW1wbGUub3JnL2NiIiwNCiAic2NvcGUiOiAib3BlbmlkIiwNCiAic3RhdGUiOiAiYWYwaWZqc2xka2oiLA0KICJub25jZSI6ICJuLTBTNl9XekEyTWoiLA0KICJtYXhfYWdlIjogODY0MDAsDQogImNsYWltcyI6IA0KICB7DQogICAidXNlcmluZm8iOiANCiAgICB7DQogICAgICJnaXZlbl9uYW1lIjogeyJlc3NlbnRpYWwiOiB0cnVlfSwNCiAgICAgIm5pY2tuYW1lIjogbnVsbCwNCiAgICAgImVtYWlsIjogeyJlc3NlbnRpYWwiOiB0cnVlfSwNCiAgICAgImVtYWlsX3ZlcmlmaWVkIjogeyJlc3NlbnRpYWwiOiB0cnVlfSwNCiAgICAgInBpY3R1cmUiOiBudWxsDQogICAgfSwNCiAgICJpZF90b2tlbiI6IA0KICAgIHsNCiAgICAgImdlbmRlciI6IG51bGwsDQogICAgICJiaXJ0aGRhdGUiOiB7ImVzc2VudGlhbCI6IHRydWV9LA0KICAgICAiYWNyIjogeyJ2YWx1ZXMiOiBbInVybjptYWNlOmluY29tbW9uOmlhcDpzaWx2ZXIiXX0NCiAgICB9DQogIH0NCn0.nwwnNsk1-ZkbmnvsF6zTHm8CHERFMGQPhos-EJcaH4Hh-sMgk8ePrGhw_trPYs8KQxsn6R9Emo_wHwajyFKzuMXZFSZ3p6Mb8dkxtVyjoy2GIzvuJT_u7PkY2t8QU9hjBcHs68PkgjDVTrG1uRTx0GxFbuPbj96tVuj11pTnmFCUR6IEOXKYr7iGOCRB3btfJhM0_AKQUfqKnRlrRscc8Kol-cSLWoYE9l5QqholImzjT_cMnNIznW9E7CDyWXTsO70xnB4SkG6pXfLSjLLlxmPGiyon_-Te111V8uE83IlzCYIb_NMXvtTIVc1jpspnTSD7xMbpL-2QgwUsAlMGzw"

        `when`("decoded") {
            val claims = Claims(
                JwtConsumerBuilder().also {
                    it.setSkipAllValidators()
                    it.setDisableRequireSignature()
                    it.setSkipSignatureVerification()
                    it.setVerificationKey(RsaJwkGenerator.generateJwk(2048).getRsaPublicKey())
                }.build().processToClaims(token).getClaimValue("claims", LinkedHashMap<String, Any>().javaClass)
            )

            then("should have given_name claim") {
                assertThat(claims.getClaim("given_name")).isNotNull
                assertThat(claims.getAllClaims().find { it.name == "given_name" }).isNotNull
            }

            then("should have nickname claim") {
                assertThat(claims.getClaim("nickname")).isNotNull
                assertThat(claims.getAllClaims().find { it.name == "nickname" }).isNotNull
            }

            then("should have email claim") {
                assertThat(claims.getClaim("email")).isNotNull
                assertThat(claims.getAllClaims().find { it.name == "email" }).isNotNull
            }

            then("should have email_verified claim") {
                assertThat(claims.getClaim("email_verified")).isNotNull
                assertThat(claims.getAllClaims().find { it.name == "email_verified" }).isNotNull
            }

            then("should have picture claim") {
                assertThat(claims.getClaim("picture")).isNotNull
                assertThat(claims.getAllClaims().find { it.name == "picture" }).isNotNull
            }

            then("should have gender claim") {
                assertThat(claims.getClaim("gender")).isNotNull
                assertThat(claims.getAllClaims().find { it.name == "gender" }).isNotNull
            }

            then("should have birthdate claim") {
                assertThat(claims.getClaim("birthdate")).isNotNull
                assertThat(claims.getAllClaims().find { it.name == "birthdate" }).isNotNull
            }

            then("should have acr claim") {
                assertThat(claims.getClaim("acr")).isNotNull
                assertThat(claims.getAllClaims().find { it.name == "acr" }).isNotNull
            }
        }
    }

    given("an empty claims") {
        val claims = Claims()
        assertThat(claims.isEmpty()).isTrue()

        `when`("require auth_time is set") {
            claims.requireAuthTime()

            then("auth_time should be part of the claims") {
                assertThat(claims.isNotEmpty()).isTrue()
                assertThat(claims.getClaim("auth_time")).isNotNull
            }
        }
    }

    given("a list of claims") {
        val claimList = listOf(
            Claim(name = "foo", essential = false, source = "id_token")
        )

        `when`("used to construct claims") {
            val claims = Claims(claimList)

            then("claims should contain items in the list") {
                assertThat(claims.getClaim("foo")).isNotNull
            }
        }
    }
})