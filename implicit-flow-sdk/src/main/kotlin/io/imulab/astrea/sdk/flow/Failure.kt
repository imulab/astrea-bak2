package io.imulab.astrea.sdk.flow

import io.imulab.astrea.sdk.oauth.error.OAuthException

fun OAuthException.toFailure(): Failure {
    return Failure.newBuilder()
        .setError(error)
        .setDescription(description)
        .setStatus(status)
        .putAllHeaders(headers)
        .build()
}