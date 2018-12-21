package io.imulab.astrea.service

import com.nhaarman.mockitokotlin2.mock
import com.typesafe.config.Config
import io.grpc.Server
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.imulab.astrea.sdk.flow.AuthorizeCodeFlowGrpc
import io.imulab.astrea.sdk.oauth.handler.OAuthAuthorizeCodeHandler
import io.imulab.astrea.sdk.oauth.reserved.ResponseType
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oauth.token.storage.AuthorizeCodeRepository
import io.imulab.astrea.sdk.oauth.token.storage.MemoryAuthorizeCodeRepository
import io.imulab.astrea.sdk.oauth.token.storage.MemoryRefreshTokenRepository
import io.imulab.astrea.sdk.oauth.token.storage.RefreshTokenRepository
import io.imulab.astrea.sdk.oidc.discovery.Discovery
import io.imulab.astrea.sdk.oidc.handler.OidcAuthorizeCodeHandler
import io.imulab.astrea.sdk.oidc.request.MemoryOidcSessionRepository
import io.imulab.astrea.sdk.oidc.request.OidcSessionRepository
import io.imulab.astrea.sdk.oidc.reserved.JweContentEncodingAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.JweKeyManagementAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.SubjectType
import io.vertx.core.Vertx
import kotlinx.coroutines.GlobalScope
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.spekframework.spek2.dsl.GroupBody
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.dsl.TestBody
import org.spekframework.spek2.style.specification.Suite
import org.spekframework.spek2.style.specification.describe
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class IntegrationTest(vertx: Vertx, config: Config) : Components(vertx, config) {

    companion object {

        fun startInProcessService(service: AuthorizeCodeFlowService): Server {
            return InProcessServerBuilder
                .forName("TestAuthorizeCodeFlowService")
                .directExecutor()
                .addService(service)
                .build().also {
                    Runtime.getRuntime().addShutdownHook(thread(start = false) {
                        it.shutdown()
                        it.awaitTermination(5, TimeUnit.SECONDS)
                    })
                }
                .start()
        }

        fun getInProcessServiceStub(): AuthorizeCodeFlowGrpc.AuthorizeCodeFlowBlockingStub {
            val channel = InProcessChannelBuilder
                .forName("TestAuthorizeCodeFlowService")
                .directExecutor()
                .build()
            return AuthorizeCodeFlowGrpc.newBlockingStub(channel)
        }
    }

    override fun bootstrap(): Kodein {
        return Kodein {
            importOnce(testDiscovery)
            importOnce(testPersistence)
            importOnce(app)

            bind<AuthorizeCodeFlowService>() with singleton {
                AuthorizeCodeFlowService(
                    coroutineContext = GlobalScope.coroutineContext,
                    authorizeHandlers = listOf(
                        instance<OAuthAuthorizeCodeHandler>(),
                        instance<OidcAuthorizeCodeHandler>()
                    ),
                    exchangeHandlers = listOf(
                        instance<OAuthAuthorizeCodeHandler>(),
                        instance<OidcAuthorizeCodeHandler>()
                    ),
                    redisAuthorizeCodeRepository = mock()
                )
            }
        }
    }

    private val testPersistence = Kodein.Module("testPersistence") {
        bind<OidcSessionRepository>() with singleton { MemoryOidcSessionRepository() }
        bind<AuthorizeCodeRepository>() with singleton { MemoryAuthorizeCodeRepository() }
        bind<RefreshTokenRepository>() with singleton { MemoryRefreshTokenRepository() }
    }

    private val testDiscovery = Kodein.Module("testDiscovery") {
        bind<Discovery>() with singleton {
            object : Discovery {
                override val issuer: String = "http://test.com"
                override val authorizationEndpoint: String = "http://test.com/oauth/authorize"
                override val tokenEndpoint: String = "http://test.com/oauth/token"
                override val userInfoEndpoint: String by lazy { notImplemented() }
                override val jwksUri: String by lazy { notImplemented() }
                override val registrationEndpoint: String by lazy { notImplemented() }
                override val scopesSupported: List<String> by lazy { notImplemented() }
                override val responseTypesSupported: List<String> = listOf(ResponseType.code)
                override val acrValuesSupported: List<String> = emptyList()
                override val subjectTypesSupported: List<String> = listOf(SubjectType.public, SubjectType.pairwise)
                override val idTokenSigningAlgorithmValuesSupported: List<String> = listOf(JwtSigningAlgorithm.RS256.spec)
                override val idTokenEncryptionAlgorithmValuesSupported: List<String> = listOf(JweKeyManagementAlgorithm.None.spec)
                override val idTokenEncryptionEncodingValuesSupported: List<String> = listOf(JweContentEncodingAlgorithm.None.spec)
                override val userInfoSigningAlgorithmValuesSupported: List<String> by lazy { notImplemented() }
                override val userInfoEncryptionAlgorithmValuesSupported: List<String> by lazy { notImplemented() }
                override val userInfoEncryptionEncodingValuesSupported: List<String> by lazy { notImplemented() }
                override val requestObjectSigningAlgorithmValuesSupported: List<String> by lazy { notImplemented() }
                override val requestObjectEncryptionAlgorithmValuesSupported: List<String> by lazy { notImplemented() }
                override val requestObjectEncryptionEncodingValuesSupported: List<String> by lazy { notImplemented() }
                override val tokenEndpointAuthenticationSigningAlgorithmValuesSupported: List<String> by lazy { notImplemented() }
                override val displayValuesSupported: List<String> by lazy { notImplemented() }
                override val claimsSupported: List<String> by lazy { notImplemented() }
                override val serviceDocumentation: String by lazy { notImplemented() }
                override val claimsLocalesSupported: List<String> by lazy { notImplemented() }
                override val uiLocalesSupported: List<String> by lazy { notImplemented() }
                override val opPolicyUri: String by lazy { notImplemented() }
            }
        }
    }
}

fun GroupBody.given(description: String, skip: Skip = Skip.No, body: Suite.() -> Unit) {
    describe("Given: $description", skip, body)
}

fun Suite.`when`(description: String, skip: Skip = Skip.No, body: Suite.() -> Unit) {
    describe("When: $description", skip, body)
}

fun Suite.then(description: String, skip: Skip = Skip.No, body: TestBody.() -> Unit) {
    it("Then $description", skip, body)
}

val config = """
service {
  id = "6ca7"
  jwks = "{\"keys\":[{\"kty\":\"RSA\",\"kid\":\"488ef553bb024059b9e4df1d38fb434f\",\"use\":\"sig\",\"alg\":\"RS256\",\"n\":\"sdxCwDIu3mj78msJSKcIrI-kF4vFnp1G5HgbLhF_hN4zUqTagEUFMm7bx1zwSlEL7ow9H7hhhvg9M0nDZcaG-nLXeI6ofvjGtXgCMw8rbzFUKs_5e3lcDrZ7EqkC9tt_sf93HFTS-pux3BlQNXDt3Sh4STiQO-5Sym5hz28DGuP3OgV2aKNYIwX3tW89KIZFegI5HyL5dsjXGOBBk0n8DYu3RwZdWB4ouEb5h5KEQzJE4HrqtqevdVPewGpJ0E4Izu8ZV8gfeUX-5almvvVSF0QMg36XN5dlSJ4T-O1wGOSl-9XIFjJlbK-6kOYloabsBVxn1i3jUjTHMqT56CzhsQ\",\"e\":\"AQAB\",\"d\":\"pf1lQo_TE_iwofZo7KgOvVU1FFB6t2Qa5GB_JUhpVSw2g7ucvDct-XpinLXM_96Rnyi8Zt0iD9-e5j3CITrdf-Er3LRu5kjw0i1VCCfxJk3IOkhskmKZmpWNWMXpIU-K5ikAFa3IVOB3Zm0tYSKqq0r4r4UdfwEVvRy1Je153V4xK1dxovnseFDpG9TOUSOZX3XNhNRyXZOwJeAAYBhbhoop4CycWZ2pkMhN8RU33RhZhuFVVApylh4INYrigCIeoXZ1wBH8yXfD3PJwpkmdS11FnFbR4Hom2vh29GotKv0zfbH_mV17codKJyHsWqHUmHxO6GRXgsc3Dfkn5dUb0Q\",\"p\":\"7Xu4yAT-lkqwZtV-7bHmDsImfuvmFTaa0-Y-ahM8_j3JogkEH2EQL9t3NDOudEWTaK04O84Ehge8zHNFXlIuqBbP3xpGGm3zzQQ1bMv8fGjr7jLqBW54HGvyRUn71vyhpI4tyAb53VZgX2yoA4hw9Pm1hOS4tjmQYWjNWUbAFq0\",\"q\":\"v7pwGn7m0eIOPyyVqeI-pZBDLEhd2QMvBbk2nInDJtwLQtvl0e92QyuzfpYY1o9EmcMifLoNopSM-pI1mV0uBzaYkcuYbOxUPGq4e5Sctng11bSoryvJqfQRIqVP8RxRnHCtGNP0Ck2oDIO11menxeOlndukQ4wGqYOKh8kHS5U\",\"dp\":\"I4iVl2gX58j5KAnd2hb6Q08Nj4QggyAOfpI-2IRUZf48wz_yG_fcAi0mYuswuTkH22u_tEMZLizONREyWfCWBpSTOeCmiKHXDuAIVmUXPE4-rv9zQKjM1APa1j9BrWNTlFmpw_o3PMUF2oFZil3J-P28CUxnzuxCDDEwOKF7cGk\",\"dq\":\"R7fy5UZhC1O3JpSD64dxDbeYGQF88YeKWGa-8fFqaafYIotlPZGIuHmpbAmzgtcWvznKNcPXA-Dzl2uH1zWO2S3oBmmVO_FvVSZKmheuQ2dWI_mvO89yYTumCUKzU6rXMGDJr0bhu9Turre6Flix3olYU2Ns38OKXnCRKlfdfb0\",\"qi\":\"2K7GD9DivD-Oif5OWNrQMyRAfHm3W1I39HjE8y3FwKfzXCtN4pUg3mZaKfBhZ3tyMhEOwCco5SHOG27Qrtc7-lpeQJN2LQiJzBtCDCQ744TSDExXYD9XW9zeQbYkSDrIEEIFJQOmeWj1vN2UQdk8pqLXCGR3ocra6Mo3rFC3MKI\"}]}"
  authorizeCodeLifespan = 10m
  authorizeCodeKey = "NjhhMjJjMGFkZjhkNDA5MThlYTVkMTMzMmNhZGNiNTY="
  refreshTokenKey = "NjhhMjJjMGFkZjhkNDA5MThlYTVkMTMzMmNhZGNiNTY="
  accessTokenLifespan = 1h
  refreshTokenLifespan = 14d
  idTokenLifespan = 1h
  stateEntropy = 8
  nonceEntropy = 8
}
""".trimIndent()