package io.imulab.astrea.service.proxy

import java.lang.RuntimeException

class RedirectionSignal(val status: Int, val url: String) : RuntimeException("Redirecting to $url with status $status.")