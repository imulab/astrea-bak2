package io.imulab.astrea.service.proxy

import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.error.ServerError
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest

@Controller
class OAuthErrorController : ErrorController {

    @Value("\${error.path:/error}")
    lateinit var errPath: String

    override fun getErrorPath(): String = errPath

    @RequestMapping(value = ["\${error.path:/error}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun error(request: HttpServletRequest) : ResponseEntity<Map<String, String>> {
        var throwable = request.getAttribute("javax.servlet.error.exception") as Throwable

        when (request.getAttribute("javax.servlet.error.status_code") as Int) {
            HttpStatus.GATEWAY_TIMEOUT.value(),
            HttpStatus.BAD_GATEWAY.value(),
            HttpStatus.SERVICE_UNAVAILABLE.value() -> throwable = ServerError.internal("service temporarily unavailable.")  // todo replace with proper error
        }

        if (throwable is RedirectionSignal)
            return ResponseEntity.status(throwable.status).header(HttpHeaders.LOCATION, throwable.url).build()

        if (throwable !is OAuthException)
            throwable = ServerError.wrapped(throwable)

        return ResponseEntity.status(throwable.status).body(throwable.data)
    }
}