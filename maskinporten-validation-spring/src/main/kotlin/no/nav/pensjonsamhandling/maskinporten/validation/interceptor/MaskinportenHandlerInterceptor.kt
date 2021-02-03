package no.nav.pensjonsamhandling.maskinporten.validation.interceptor

import com.nimbusds.jwt.JWTParser
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator
import no.nav.pensjonsamhandling.maskinporten.validation.annotation.Maskinporten
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganizationValidator
import org.springframework.beans.factory.BeanExpressionException
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class MaskinportenHandlerInterceptor(
    private val maskinportenValidator: MaskinportenValidator,
    private val validators: List<RequestAwareOrganizationValidator>
) : HandlerInterceptor {

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
        val validator = validators.firstOrNull { validatorType == it }
            ?: throw BeanExpressionException("No bean of type $validatorType exists. Did you remember to annotate the class as a @Component?")
        val jwt = request.getHeader("authentication")
            ?.run { if (startsWith("bearer ")) removePrefix("bearer ") else null }
            ?.let { JWTParser.parse(it)!! }
        return maskinportenValidator(jwt!!, requiredScope, validator, request)
    }
}