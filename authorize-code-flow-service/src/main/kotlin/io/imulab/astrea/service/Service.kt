package io.imulab.astrea.service

import com.typesafe.config.Config
import io.grpc.stub.StreamObserver
import io.imulab.astrea.sdk.flow.*
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.imulab.astrea.sdk.oauth.handler.AccessRequestHandler
import io.imulab.astrea.sdk.oauth.handler.AuthorizeRequestHandler
import io.imulab.astrea.sdk.oauth.request.OAuthAccessRequest
import io.imulab.astrea.sdk.oauth.validation.OAuthRequestValidationChain
import io.imulab.astrea.sdk.oidc.request.OidcAuthorizeRequest
import io.imulab.astrea.sdk.oidc.request.OidcSession
import io.imulab.astrea.sdk.oidc.response.OidcAuthorizeEndpointResponse
import io.imulab.astrea.sdk.oidc.response.OidcTokenEndpointResponse
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.grpc.VertxServerBuilder
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext

class GrpcVerticle(
    private val flowService: AuthorizeCodeFlowService,
    private val appConfig: Config
) : AbstractVerticle() {

    private val logger = LoggerFactory.getLogger(GrpcVerticle::class.java)

    override fun start(startFuture: Future<Void>?) {
        val server = VertxServerBuilder
            .forPort(vertx, appConfig.getInt("service.port"))
            .addService(flowService)
            .build()

        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            logger.info("GrpcVerticle shutting down...")
            server.shutdown()
            server.awaitTermination(10, TimeUnit.SECONDS)
        })

        server.start { ar ->
            if (ar.failed()) {
                logger.error("GrpcVerticle failed to start.", ar.cause())
            } else {
                startFuture?.complete()
                logger.info("GrpcVerticle started...")
            }
        }
    }
}

class AuthorizeCodeFlowService(
    private val concurrency: Int = 4,
    override val coroutineContext: CoroutineContext = Executors.newFixedThreadPool(concurrency).asCoroutineDispatcher(),
    private val authorizeHandlers: List<AuthorizeRequestHandler>,
    private val exchangeHandlers: List<AccessRequestHandler>,
    private val redisAuthorizeCodeRepository: RedisAuthorizeCodeRepository,
    private val authorizeValidation: OAuthRequestValidationChain,
    private val exchangeValidation: OAuthRequestValidationChain
) : AuthorizeCodeFlowGrpc.AuthorizeCodeFlowImplBase(), CoroutineScope {

    override fun authorize(request: CodeRequest?, responseObserver: StreamObserver<CodeResponse>?) {
        if (request == null || responseObserver == null)
            return

        val job = Job()
        val authorizeRequest = OidcAuthorizeRequest.Builder().apply {
            client = CodeFlowAuthorizeLegClient(request.client)
            responseTypes.addAll(request.responseTypesList)
            redirectUri = request.redirectUri
            scopes.addAll(request.scopesList)
            state = request.state
            session = OidcSession().also { s ->
                request.session.takeIf { it != null }?.let { rs ->
                    s.subject = rs.subject
                    s.authTime = LocalDateTime.ofEpochSecond(rs.authenticationTime, 0, ZoneOffset.UTC)
                    s.nonce = rs.nonce
                    s.grantedScopes.addAll(rs.grantedScopesList)
                    s.acrValues.addAll(rs.acrValuesList)
                }
            }
            nonce = session.nonce
        }.build()
        val authorizeResponse = OidcAuthorizeEndpointResponse()

        launch(job) {
            authorizeValidation.validate(authorizeRequest)

            authorizeHandlers.forEach { h ->
                h.handleAuthorizeRequest(authorizeRequest, authorizeResponse)
            }

            if (!authorizeResponse.handledResponseTypes.containsAll(authorizeRequest.responseTypes))
                throw ServerError.internal("Some response types were not handled.")
        }.invokeOnCompletion { t ->
            if (t != null) {
                job.cancel()
                val e: OAuthException = if (t is OAuthException) t else ServerError.wrapped(t)
                responseObserver.onNext(
                    CodeResponse.newBuilder()
                        .setSuccess(false)
                        .setFailure(e.toFailure())
                        .build()
                )
            } else {
                responseObserver.onNext(
                    CodeResponse.newBuilder()
                        .setSuccess(true)
                        .setData(
                            CodePackage.newBuilder()
                                .setCode(authorizeResponse.code)
                                .addAllScopes(authorizeResponse.scope)
                                .build()
                        )
                        .build()
                )
            }

            responseObserver.onCompleted()
        }
    }

    override fun exchange(request: TokenRequest?, responseObserver: StreamObserver<TokenResponse>?) {
        if (request == null || responseObserver == null)
            return

        val job = Job()
        val tokenRequest = OAuthAccessRequest.Builder().apply {
            client = ClientFlowTokenLegClient(request.client)
            grantTypes.add(request.grantType)
            redirectUri = request.redirectUri
            code = request.code
            session = OidcSession()
        }.build()
        val tokenResponse = OidcTokenEndpointResponse()

        launch(job) {
            exchangeValidation.validate(tokenRequest)

            exchangeHandlers.forEach { h -> h.updateSession(tokenRequest) }
            exchangeHandlers.forEach { h -> h.handleAccessRequest(tokenRequest, tokenResponse) }

            // safety mechanism in case the incoming request is OAuth only.
            // since the repository impl waits for the oidc handler to delete session, we may neglect
            // session deletion if the request is not OIDC scoped.
            // hence, as a safety, we force delete again here.
            redisAuthorizeCodeRepository.deleteOidcSession(tokenRequest.code)
        }.invokeOnCompletion { t ->
            if (t != null) {
                job.cancel()
                val e: OAuthException = if (t is OAuthException) t else ServerError.wrapped(t)
                responseObserver.onNext(
                    TokenResponse.newBuilder()
                        .setSuccess(false)
                        .setFailure(e.toFailure())
                        .build()
                )
            } else {
                responseObserver.onNext(
                    TokenResponse.newBuilder()
                        .setSuccess(true)
                        .setData(
                            TokenPackage.newBuilder()
                                .setAccessToken(tokenResponse.accessToken)
                                .setExpiresIn(tokenResponse.expiresIn)
                                .setTokenType(tokenResponse.tokenType)
                                .setRefreshToken(tokenResponse.refreshToken)
                                .setIdToken(tokenResponse.idToken)
                                .build()
                        )
                        .build()
                )
            }
            responseObserver.onCompleted()
        }
    }
}