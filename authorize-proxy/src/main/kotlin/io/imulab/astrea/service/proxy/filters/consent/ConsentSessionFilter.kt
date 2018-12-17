package io.imulab.astrea.service.proxy.filters.consent

import com.netflix.zuul.context.RequestContext
import io.imulab.astrea.sdk.oauth.token.setScope
import io.imulab.astrea.service.proxy.*
import io.imulab.astrea.service.proxy.filters.login.LoginFilter.Companion.LoginClaims
import org.jose4j.jwt.JwtClaims
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ConsentSessionFilter : ConsentFilter() {

    @Autowired
    lateinit var sessionRepository: SessionRepositoryAdapter

    override fun run(): Any {
        val context = RequestContext.getCurrentContext()

        val sessionCookie = context.request.cookies.find { it.name == ConsentSessionName } ?: return Unit
        val session = sessionRepository.findById(sessionCookie.value) ?: return Unit

        if ((context[LoginClaims] as? JwtClaims)?.subject != session.subject)
            return Unit
        else if (session.hasConsentExpired()) {
            sessionRepository.deleteById(session.id)
            return Unit
        }

        setConsentClaims(JwtClaims().also { c ->
            c.setScope(session.consentScope)
            c.setClaim("claims", session.consentClaims)
        })

        return Unit
    }

    override fun filterOrder(): Int = BaseOrder
}