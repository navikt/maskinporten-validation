package no.nav.pensjonsamhandling.maskinporten.interceptor;

import com.nimbusds.jwt.JWTParser
import no.nav.pensjonsamhandling.maskinporten.annotation.Maskinporten
import no.nav.pensjonsamhandling.maskinporten.orgno.RequestAwareOrganizationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator
import org.springframework.stereotype.Service
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class MaskinportenHandlerInterceptor(
    private val maskinportenValidator: MaskinportenValidator,
    private val validators: List<RequestAwareOrganizationValidator>
): HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        if (handler !is HandlerMethod) return true
        val annotation = handler.getMethodAnnotation(Maskinporten::class.java)
            ?: handler.method.declaringClass.getAnnotation(Maskinporten::class.java)
        val requiredScope = annotation.scope
        val validatorType = annotation.orgValidator
        val validator = validators.first { validatorType == it }
        val jwt = request.getHeader("authentication")
            ?.run { if (startsWith("bearer ")) removePrefix("bearer ") else null }
            ?.let { JWTParser.parse(it)!! }
        return maskinportenValidator(jwt!!, requiredScope, validator, request)
    }
}