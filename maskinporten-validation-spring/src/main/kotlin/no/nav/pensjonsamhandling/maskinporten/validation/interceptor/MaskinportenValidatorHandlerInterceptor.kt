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
        handler.maskinportenAnnotation?.preHandle(request) ?: true -> true
        else -> {
            LOG.debug("Rejected by orgnr validator.\nValidator class: {}", handler.validatorClassname)
            throw ResponseStatusException(FORBIDDEN)
        }
    }

    private fun Maskinporten.preHandle(request: HttpServletRequest) = try {
        LOG.debug("Received request for {}", request.requestURI)
        maskinportenValidator(request.bearerToken, scope, validator, request)
    } catch (e: Exception) {
        LOG.debug("Failed to validate token.", e)
        try {
            LOG.debug("Token claims:\n{}", request.bearerToken.jwtClaimsSet)
        } catch (_: Exception) {
            LOG.debug("Missing bearer token.")
        }
        throw ResponseStatusException(UNAUTHORIZED)
    }

    private val HandlerMethod.validatorClassname: String?
        get() = maskinportenAnnotation?.orgValidator?.qualifiedName

    private val HandlerMethod.maskinportenAnnotation: Maskinporten?
        get() = getMethodAnnotation(Maskinporten::class.java)
            ?: method.declaringClass.getAnnotation(Maskinporten::class.java)

    private val Maskinporten.validator: RequestAwareOrganisationValidator
        get() = validators.firstOrNull(orgValidator::isInstance)
            ?: throw BeanExpressionException("No bean of type $orgValidator exists. Did you remember to annotate the class as a @Component?")

    private val HttpServletRequest.bearerToken: JWT
        get() = JWTParser.parse(getHeader("authorization")?.substringAfter("Bearer ", ""))

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(MaskinportenValidatorHandlerInterceptor::class.java)
    }
}