package no.nav.pensjonsamhandling.maskinporten.validation.interceptor

import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTParser
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator
import no.nav.pensjonsamhandling.maskinporten.validation.MissingScopeException
import no.nav.pensjonsamhandling.maskinporten.validation.annotation.Maskinporten
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.pid.RequestAwarePidValidator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanExpressionException
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.method.HandlerMethod
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.HandlerInterceptor

class MaskinportenValidatorHandlerInterceptor(
    private val maskinportenValidator: List<MaskinportenValidator>,
    private val organisationValidators: List<RequestAwareOrganisationValidator>,
    private val pidValidators: List<RequestAwarePidValidator>,
) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ) = when {
        handler !is HandlerMethod -> true
        handler.maskinportenAnnotation?.preHandle(request) ?: true -> true
        else -> throw ResponseStatusException(FORBIDDEN)
    }

    private fun Maskinporten.preHandle(request: HttpServletRequest) = try {
        LOG.debug("Received request for: {}", request.requestURI)
        val env = maskinportenValidator.firstOrNull { it.environment.baseURL.toString() == request.bearerToken.jwtClaimsSet.issuer }
            ?: maskinportenValidator.first()
        env(request.bearerToken, scope, organisationValidator, pidValidator, request).apply {
            if(accepted) {
                LOG.debug("Accepted.")
                RequestContextHolder.currentRequestAttributes().apply {
                    if (pid != null) setAttribute("pid", pid!!, SCOPE_REQUEST)
                    setAttribute("orgno", orgno, SCOPE_REQUEST)
                }
            }
            else LOG.debug("Rejected.")
        }.accepted
    }
    catch (e: MissingScopeException) {
        LOG.debug("Missing required scope.", e)
        throw ResponseStatusException(FORBIDDEN)
    }
    catch (e: Exception) {
        LOG.debug("Failed to validate token.", e)
        try {
            LOG.debug("Token claims:\n{}", request.bearerToken.jwtClaimsSet)
        } catch (_: Exception) {
            LOG.debug("Missing bearer token.")
        }
        throw ResponseStatusException(UNAUTHORIZED)
    }

    private val HandlerMethod.maskinportenAnnotation: Maskinporten?
        get() = getMethodAnnotation(Maskinporten::class.java)
            ?: method.declaringClass.getAnnotation(Maskinporten::class.java)

    private val Maskinporten.organisationValidator: RequestAwareOrganisationValidator
        get() = organisationValidators.firstOrNull(orgValidatorClass::isInstance)
            ?.also { LOG.debug("Orgnr validator: {}", it::class.qualifiedName) }
            ?: throw BeanExpressionException("No bean of type $orgValidatorClass exists. Did you remember to annotate the class as a @Component?")

    private val Maskinporten.pidValidator: RequestAwarePidValidator
        get() = pidValidators.firstOrNull(pidValidatorClass::isInstance)
            ?.also { LOG.debug("PID validator: {}", it::class.qualifiedName) }
            ?: throw BeanExpressionException("No bean of type $pidValidatorClass exists. Did you remember to annotate the class as a @Component?")

    private val HttpServletRequest.bearerToken: JWT
        get() = JWTParser.parse(getHeader("authorization")?.substringAfter("Bearer ", ""))

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(MaskinportenValidatorHandlerInterceptor::class.java)
    }
}
