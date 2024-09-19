package no.nav.pensjonsamhandling.maskinporten.validation

import com.nimbusds.jose.JWSAlgorithm.RS256
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.jwk.source.JWKSourceBuilder
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jose.util.DefaultResourceRetriever
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import no.nav.pensjonsamhandling.maskinporten.validation.config.Environment
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.OrganisationValidator
import java.net.Proxy
import java.text.ParseException

open class MaskinportenValidator(
    val environment: Environment = Environment.Prod,
    private val proxy: Proxy? = null,
    private val permitAll: List<String> = emptyList()
) {
    var jwkSet: JWKSource<SecurityContext> = JWKSourceBuilder.create<SecurityContext>(
        environment.baseURL.toURI().resolve("/jwk").toURL(),
        DefaultResourceRetriever().apply {
            proxy = this@MaskinportenValidator.proxy
        }).build()
        set(value) {
            field = value
            jwtProcessor = DefaultJWTProcessor<SecurityContext>().apply {
                jwsKeySelector = generateKeySelector()
                jwtClaimsSetVerifier = generateClaimsVerifier()
            }
        }

    private var jwtProcessor = DefaultJWTProcessor<SecurityContext>().apply {
        jwsKeySelector = generateKeySelector()
        jwtClaimsSetVerifier = generateClaimsVerifier()
    }

    private fun generateKeySelector() = JWSVerificationKeySelector(RS256, jwkSet)

    private fun <c: SecurityContext> generateClaimsVerifier() = DefaultJWTClaimsVerifier<c>(
        JWTClaimsSet.Builder()
            .issuer(environment.baseURL.toExternalForm().postfix('/'))
            .build(),
        setOf("client_id", "client_amr", CONSUMER_CLAIM, "exp", "iat", "jti")
    )

    /**
     * @return Organisation to whom the token belongs.
     */
    operator fun invoke(token: JWT, requiredScope: String) =
        jwtProcessor.process(token, null)
            .takeIf { it.hasScope(requiredScope) }
            ?.orgno
            ?: throw MissingScopeException("Token missing required scope.")

    operator fun <T> invoke(
        token: JWT, requiredScope: String,
        organisationValidator: OrganisationValidator<T>,
        o: T
    ) = this(token, requiredScope).let { orgno ->
        orgno in permitAll || organisationValidator(orgno, o)
    }

    private val JWTClaimsSet.orgno: String?
        get() = consumer?.get("ID")?.toString()?.substringAfterLast(':')

    @Suppress("unused")
    private val JWTClaimsSet.supplier: Map<String, Any>?
        get() = getJSONObjectClaim(SUPPLIER_CLAIM)

    private val JWTClaimsSet.consumer: Map<String, Any>?
        get() = getJSONObjectClaim(CONSUMER_CLAIM)

    private fun JWTClaimsSet.hasScope(requiredScope: String) = try {
        requiredScope == getStringClaim(SCOPE_CLAIM)
    } catch (_: ParseException) {
        requiredScope in getStringArrayClaim(SCOPE_CLAIM)
    } catch (_: ParseException) {
        requiredScope in getStringListClaim(SCOPE_CLAIM)
    }


    companion object {
        const val SCOPE_CLAIM = "scope"
        const val CONSUMER_CLAIM = "consumer"
        const val SUPPLIER_CLAIM = "supplier"
    }
}
