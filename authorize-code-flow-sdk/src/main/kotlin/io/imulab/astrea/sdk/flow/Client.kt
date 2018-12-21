package io.imulab.astrea.sdk.flow

import io.imulab.astrea.sdk.oidc.client.OidcClient

fun OidcClient.asCodeRequestClient(): CodeRequest.Client =
        CodeRequest.Client.newBuilder()
            .setId(this.id)
            .addAllRedirectUris(this.redirectUris)
            .addAllScopes(this.scopes)
            .addAllResponseTypes(this.responseTypes)
            .build()

fun OidcClient.asTokenRequestClient(): TokenRequest.Client =
        TokenRequest.Client.newBuilder()
            .setId(this.id)
            .addAllGrantTypes(this.grantTypes)
            .addAllRedirectUris(this.redirectUris)
            .setJwks(this.jwks)
            .setSectorIdentifierUri(this.sectorIdentifierUri)
            .setSubjectType(this.subjectType)
            .setIdTokenSignedResponseAlgorithm(this.idTokenSignedResponseAlgorithm.spec)
            .setIdTokenEncryptedResponseAlgorithm(this.idTokenEncryptedResponseAlgorithm.spec)
            .setIdTokenEncryptedResponseEncoding(this.idTokenEncryptedResponseEncoding.spec)
            .build()