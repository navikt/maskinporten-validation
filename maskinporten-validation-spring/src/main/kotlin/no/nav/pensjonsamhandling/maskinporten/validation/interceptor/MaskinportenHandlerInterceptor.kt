package no.nav.pensjonsamhandling.maskinporten.validation.interceptor

import com.nimbusds.jwt.JWTParser
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator
import no.nav.pensjonsamhandling.maskinporten.validation.annotation.Maskinporten
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganizationValidator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanExpressionException
import org.springframework.http.HttpStatus
import org.springframework.web.method.HandlerMethod
import org.springframework.web.server.ResponseStatusException
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
        val validator = validators.firstOrNull { it::class == validatorType }
            ?: throw BeanExpressionException("No bean of type $validatorType exists. Did you remember to annotate the class as a @Component?")
        val jwt = try {
            request.getHeader("authorization")
                ?.run { if (startsWith("Bearer ")) removePrefix("Bearer ") else null }
                .let { JWTParser.parse(it)!! }
        } catch (e: Exception) {
            LOG.debug("Failed to validate token.", e)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }
        return if (maskinportenValidator(
                jwt,
                requiredScope,
                validator,
                request
            )
        ) true else throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(MaskinportenHandlerInterceptor::class.java)
    }
}