package no.nav.pensjonsamhandling.maskinporten.validation

import com.nimbusds.jose.JWSAlgorithm.RS256
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.jwk.source.JWKSourceBuilder
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jose.shaded.gson.Gson
import com.nimbusds.jose.util.DefaultResourceRetriever
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import no.nav.pensjonsamhandling.maskinporten.validation.config.Environment
import no.nav.pensjonsamhandling.maskinporten.validation.consent.AuthorizationDetails
import no.nav.pensjonsamhandling.maskinporten.validation.consent.ConsentValidator
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.OrganisationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.PidValidator
import java.net.Proxy
import java.text.ParseException
import java.time.Instant
import java.util.Date

open class MaskinportenValidator(
    val environment: Environment = Environment.Prod,
    private val proxy: Proxy? = null,
    private val permitAll: List<String> = emptyList(),
    private val requirePid: Boolean = false,
    private val requireConsent: Boolean = false
) {
    private val requiredClaims = setOfNotNull(
        "client_id",
        "client_amr",
        CONSUMER_CLAIM,
        "exp",
        "iat",
        "jti",
        if (requirePid) PID_CLAIM else null,
        if (requireConsent) AUTHORIZATION_DETAILS_CLAIM else null
    )

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
        requiredClaims
    )

    /**
     * @return Organisation to whom the token belongs.
     */
    operator fun invoke(token: JWT, requiredScope: String, requiredSamtykke: String? = null) =
        process(token, requiredScope, requiredSamtykke).orgno


    operator fun <T> invoke(
        token: JWT, requiredScope: String, requiredConsent: String?,
        organisationValidator: OrganisationValidator<T>,
        pidValidator: PidValidator<T>,
        consentValidator: ConsentValidator<T>,
        o: T
    ) = process(token, requiredScope, requiredConsent).run {
        val consent = findConsent(o, consentValidator)
        ValidationResult(
            (orgno in permitAll || organisationValidator(orgno, o))
                    && pidValidator(pid, o)
                    && consent.first,
            orgno,
            pid,
            consent.second?.from
        )
    }

    private fun process(token: JWT, requiredScope: String, requiredConsent: String?) = jwtProcessor.process(token, null).apply {
        if (!hasScope(requiredScope)) throw MissingScopeException(requiredScope)
        if (requiredConsent != null && validAuthorizationDetails.none { it.has(requiredConsent) }) throw MissingConsentException(requiredConsent)
    }

    private fun <T> JWTClaimsSet.findConsent(o: T, consentValidator: ConsentValidator<T>) = validAuthorizationDetails.run {
        if (isEmpty()) consentValidator(null, o) to null else firstOrNull { consentValidator(it, o) }?.let {
            true to it
        } ?: (false to null)
    }

    private val JWTClaimsSet.validAuthorizationDetails: Collection<AuthorizationDetails>
        get() = authorizationDetails.filter {
            it.to.orgno == orgno && it.validTo.after(Date.from(Instant.now()))
        }

    private val JWTClaimsSet.orgno: String
        get() = consumer["ID"]?.toString()?.substringAfterLast(':')
            ?: throw MalformedConsumerException(consumer.toString())

    @Suppress("unused")
    private val JWTClaimsSet.supplier: Map<String, Any>?
        get() = getJSONObjectClaim(SUPPLIER_CLAIM)

    private val JWTClaimsSet.consumer: Map<String, Any>
        get() = getJSONObjectClaim(CONSUMER_CLAIM)

    private val JWTClaimsSet.pid: String?
        get() = getStringClaim(PID_CLAIM)

    private val JWTClaimsSet.authorizationDetails: Collection<AuthorizationDetails>
        get() = Gson().toJsonTree(getClaim(AUTHORIZATION_DETAILS_CLAIM) ?: emptyList<Any>()).asJsonArray.map {
            AuthorizationDetails(it.asJsonObject)
        }

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
        const val PID_CLAIM = "pid"
        const val AUTHORIZATION_DETAILS_CLAIM = "authorization_details"
    }
}
