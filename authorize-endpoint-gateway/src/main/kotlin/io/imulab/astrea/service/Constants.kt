package io.imulab.astrea.service

object RoutingContextAttribute {
    const val authentication = "authentication"
    const val authorization = "authorization"
    const val parameterHash = "param_hash"
    const val verifiedParameterLock = "verified_param_lock"
}

object Stage {
    const val name = "stage"
    const val authentication = 1
    const val authorization = 2
}

object Params {
    const val parameterLock = "param_lock"
    const val loginToken = "login_token"
    const val consentToken = "consent_token"
}