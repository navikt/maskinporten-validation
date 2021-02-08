package no.nav.pensjonsamhandling.maskinporten.validation.interceptor

import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTParser
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator
import no.nav.pensjonsamhandling.maskinporten.validation.annotation.Maskinporten
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanExpressionException
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.web.method.HandlerMethod
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class MaskinportenValidatorHandlerInterceptor(
    private val maskinportenValidator: MaskinportenValidator,
    private val validators: List<RequestAwareOrganisationValidator>
) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ) = when {
        handler !is HandlerMethod -> true
        preHandle(request, handler) -> true
        else -> throw ResponseStatusException(FORBIDDEN)
    }

    private fun preHandle(
        request: HttpServletRequest,
        handler: HandlerMethod
    ) = try {
        val annotation = handler.maskinportenAnnotation

        val validator = validators.firstOrNull(annotation.orgValidator::isInstance)
            ?: throw BeanExpressionException("No bean of type ${annotation.orgValidator} exists. Did you remember to annotate the class as a @Component?")

        maskinportenValidator(request.bearerToken, annotation.scope, validator, request)
    } catch (e: Exception) {
        LOG.debug("Failed to validate token.", e)
        throw ResponseStatusException(UNAUTHORIZED)
    }

    private val HandlerMethod.maskinportenAnnotation: Maskinporten
        get() = getMethodAnnotation(Maskinporten::class.java)
            ?: method.declaringClass.getAnnotation(Maskinporten::class.java)

    private val HttpServletRequest.bearerToken: JWT
        get() = JWTParser.parse(getHeader("authorization")?.substringAfter("Bearer ", ""))

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(MaskinportenValidatorHandlerInterceptor::class.java)
    }
}