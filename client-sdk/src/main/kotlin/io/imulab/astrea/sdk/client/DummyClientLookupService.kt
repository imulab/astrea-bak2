package io.imulab.astrea.sdk.client

import io.grpc.stub.StreamObserver
import io.imulab.astrea.sdk.oauth.error.InvalidClient
import io.imulab.astrea.sdk.oauth.reserved.*
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.ApplicationType
import io.imulab.astrea.sdk.oidc.reserved.JweContentEncodingAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.JweKeyManagementAlgorithm
import io.imulab.astrea.sdk.oidc.reserved.SubjectType

object DummyClientLookupService : ClientLookupGrpc.ClientLookupImplBase() {

    const val serviceName = "DummyClientLookupService"

    val prototype: ClientLookupResponse = ClientLookupResponse.newBuilder()
        .setName("Always Found Client")
        .setType(ClientType.confidential)
        .addRedirectUris("https://foo.imulab.com/callback")
        .addResponseTypes(ResponseType.code)
        .addResponseTypes(ResponseType.token)
        .addResponseTypes(io.imulab.astrea.sdk.oidc.reserved.ResponseType.idToken)
        .addGrantTypes(GrantType.authorizationCode)
        .addGrantTypes(GrantType.clientCredentials)
        .addGrantTypes(GrantType.implicit)
        .addGrantTypes(GrantType.refreshToken)
        .addScopes("foo")
        .addScopes("bar")
        .addScopes(StandardScope.offlineAccess)
        .addScopes(io.imulab.astrea.sdk.oidc.reserved.StandardScope.openid)
        .setApplicationType(ApplicationType.web)
        .addContacts("imulab@foo.com")
        .setJwks("{}")
        .setSectorIdentifierUri("https://foo.imulab.com")
        .setSubjectType(SubjectType.pairwise)
        .setIdTokenSignedResponseAlgorithm(JwtSigningAlgorithm.RS256.spec)
        .setIdTokenEncryptedResponseAlgorithm(JweKeyManagementAlgorithm.None.spec)
        .setIdTokenEncryptedResponseEncoding(JweContentEncodingAlgorithm.None.spec)
        .setRequestObjectSigningAlgorithm(JwtSigningAlgorithm.RS256.spec)
        .setRequestObjectEncryptionAlgorithm(JweKeyManagementAlgorithm.None.spec)
        .setRequestObjectEncryptionEncoding(JweContentEncodingAlgorithm.None.spec)
        .setUserInfoSignedResponseAlgorithm(JwtSigningAlgorithm.RS256.spec)
        .setUserInfoEncryptedResponseAlgorithm(JweKeyManagementAlgorithm.None.spec)
        .setUserInfoEncryptedResponseEncoding(JweContentEncodingAlgorithm.None.spec)
        .setTokenEndpointAuthenticationMethod(AuthenticationMethod.clientSecretPost)
        .setDefaultMaxAge(600)
        .setRequireAuthTime(true)
        .build()

    override fun find(request: ClientLookupRequest?, responseObserver: StreamObserver<ClientLookupResponse>?) {
        if (request == null) {
            responseObserver?.onError(InvalidClient.unknown())
            return
        }

        responseObserver?.onNext(
            ClientLookupResponse
                .newBuilder(prototype)
                .setId(request.id)
                .build()
        )
        responseObserver?.onCompleted()
    }
}