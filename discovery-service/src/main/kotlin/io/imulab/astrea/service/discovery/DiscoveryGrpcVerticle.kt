package io.imulab.astrea.service.discovery

import io.grpc.stub.StreamObserver
import io.imulab.astrea.sdk.discovery.Discovery
import io.imulab.astrea.sdk.discovery.DiscoveryGrpc
import io.imulab.astrea.sdk.discovery.DiscoveryRequest
import io.imulab.astrea.sdk.discovery.DiscoveryResponse
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.grpc.VertxServerBuilder
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class DiscoveryGrpcVerticle(discovery: Discovery) : AbstractVerticle() {

    private val discoveryResponse = DiscoveryResponse.newBuilder()
        .setIssuer(discovery.issuer)
        .setAuthorizationEndpoint(discovery.authorizationEndpoint)
        .setTokenEndpoint(discovery.tokenEndpoint)
        .setUserInfoEndpoint(discovery.userInfoEndpoint)
        .setJwksUri(discovery.jwksUri)
        .setRegistrationEndpoint(discovery.registrationEndpoint)
        .addAllScopesSupported(discovery.scopesSupported)
        .addAllResponseTypesSupported(discovery.responseTypesSupported)
        .addAllResponseModeSupported(discovery.responseModeSupported)
        .addAllGrantTypesSupported(discovery.grantTypesSupported)
        .addAllAcrValuesSupported(discovery.acrValuesSupported)
        .addAllSubjectTypesSupported(discovery.subjectTypesSupported)
        .addAllIdTokenSigningAlgorithmValuesSupported(discovery.idTokenSigningAlgorithmValuesSupported)
        .addAllIdTokenEncryptionAlgorithmValuesSupported(discovery.idTokenEncryptionAlgorithmValuesSupported)
        .addAllIdTokenEncryptionEncodingValuesSupported(discovery.idTokenEncryptionEncodingValuesSupported)
        .addAllUserInfoSigningAlgorithmValuesSupported(discovery.userInfoSigningAlgorithmValuesSupported)
        .addAllUserInfoEncryptionAlgorithmValuesSupported(discovery.userInfoEncryptionAlgorithmValuesSupported)
        .addAllUserInfoEncryptionEncodingValuesSupported(discovery.userInfoEncryptionEncodingValuesSupported)
        .addAllRequestObjectSigningAlgorithmValuesSupported(discovery.requestObjectSigningAlgorithmValuesSupported)
        .addAllRequestObjectEncryptionAlgorithmValuesSupported(discovery.requestObjectEncryptionAlgorithmValuesSupported)
        .addAllRequestObjectEncryptionEncodingValuesSupported(discovery.requestObjectEncryptionEncodingValuesSupported)
        .addAllTokenEndpointAuthenticationMethodsSupported(discovery.tokenEndpointAuthenticationMethodsSupported)
        .addAllTokenEndpointAuthenticationSigningAlgorithmValuesSupported(discovery.tokenEndpointAuthenticationSigningAlgorithmValuesSupported)
        .addAllDisplayValuesSupported(discovery.displayValuesSupported)
        .addAllClaimTypesSupported(discovery.claimTypesSupported)
        .addAllClaimsSupported(discovery.claimsSupported)
        .addAllUiLocalesSupported(discovery.uiLocalesSupported)
        .setClaimsParameterSupported(discovery.claimsParameterSupported)
        .setRequestParameterSupported(discovery.requestParameterSupported)
        .setRequestUriParameterSupported(discovery.requestUriParameterSupported)
        .setRequireRequestUriRegistration(discovery.requireRequestUriRegistration)
        .setOpPolicyUri(discovery.opPolicyUri)
        .build()

    override fun start(startFuture: Future<Void>?) {
        val server = VertxServerBuilder
            .forPort(vertx, 35028)
            .addService(service)
            .build()

        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            server.shutdown()
            server.awaitTermination(10, TimeUnit.SECONDS)
        })

        server.start(startFuture?.completer())
    }

    private val service = object : DiscoveryGrpc.DiscoveryImplBase() {
        override fun get(request: DiscoveryRequest?, responseObserver: StreamObserver<DiscoveryResponse>?) {
            responseObserver?.onNext(discoveryResponse)
            responseObserver?.onCompleted()
        }
    }
}