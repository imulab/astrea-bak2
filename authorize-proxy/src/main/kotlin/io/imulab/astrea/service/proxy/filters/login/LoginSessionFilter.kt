package io.imulab.astrea.service.proxy.filters.login

import com.netflix.zuul.context.RequestContext
import io.imulab.astrea.sdk.oidc.jwk.setAuthTime
import io.imulab.astrea.service.proxy.LoginSession
import org.jose4j.jwt.JwtClaims
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.session.SessionRepository
import org.springframework.stereotype.Component

@Component
class LoginSessionFilter : LoginFilter() {

    @Autowired
    lateinit var sessionRepository: SessionRepository<*>

    override fun run(): Any {
        val context = RequestContext.getCurrentContext()

        val sessionCookie = context.request.cookies.find { it.name == "ASTREA_SESSION" } ?: return Unit
        val rawSession = sessionRepository.findById(sessionCookie.value) ?: return Unit

        val session = LoginSession(rawSession)
        if (session.hasAuthenticationExpired()) {
            sessionRepository.deleteById(session.id)
            return Unit
        }

        setLoginClaims(JwtClaims().also { c ->
            c.subject = session.subject
            c.setAuthTime(session.authTime)
        })

        return Unit
    }

    override fun filterOrder(): Int = BaseOrder
}