package no.nav.pensjonsamhandling.maskinporten.validation

import com.nimbusds.jose.JWSAlgorithm.RS256
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.BadJWTException
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import net.minidev.json.JSONObject
import no.nav.pensjonsamhandling.maskinporten.validation.config.MaskinportenValidatorConfig
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.OrganisationValidator
import java.text.ParseException

open class MaskinportenValidator(
    private val maskinportenValidatorConfig: MaskinportenValidatorConfig
) {

    private val jwtProcessor = DefaultJWTProcessor<SecurityContext>().apply {
        jwsKeySelector = JWSVerificationKeySelector(
            RS256,
            maskinportenValidatorConfig.jwkSet
        )
        jwtClaimsSetVerifier = DefaultJWTClaimsVerifier(
            JWTClaimsSet.Builder()
                .issuer(maskinportenValidatorConfig.baseURL.toExternalForm().postfix('/'))
                .build(),
            setOf("client_id", "client_amr", CONSUMER_CLAIM, "exp", "iat", "jti")
        )
    }

    /**
     * @return Organisation to whom the token belongs.
     */
    operator fun invoke(token: JWT, requiredScope: String) =
        jwtProcessor.process(token, null)
            .takeIf {
                try {
                    requiredScope == it.getStringClaim(SCOPE_CLAIM)
                } catch (_: ParseException) {
                    requiredScope in it.getStringArrayClaim(SCOPE_CLAIM)
                }
            }
            ?.orgno
            ?: throw BadJWTException("Token missing required scope.")

    operator fun <T> invoke(
        token: JWT, requiredScope: String,
        organisationValidator: OrganisationValidator<T>,
        o: T
    ) = organisationValidator(this(token, requiredScope), o)

    private val JWTClaimsSet.orgno: String?
        get() = supplier ?: consumer

    private val JWTClaimsSet.supplier: String?
        get() = (getClaim(SUPPLIER_CLAIM) as JSONObject?)?.getAsString("ID")?.substringAfterLast(':')

    private val JWTClaimsSet.consumer: String?
        get() = (getClaim(CONSUMER_CLAIM) as JSONObject?)?.getAsString("ID")?.substringAfterLast(':')


    companion object {
        const val SCOPE_CLAIM = "scope"
        const val CONSUMER_CLAIM = "consumer"
        const val SUPPLIER_CLAIM = "supplier"
    }
}