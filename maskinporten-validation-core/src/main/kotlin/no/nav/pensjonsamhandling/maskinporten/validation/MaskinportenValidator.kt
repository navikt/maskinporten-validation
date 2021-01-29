package no.nav.pensjonsamhandling.maskinporten.validation

import com.nimbusds.jose.JWSAlgorithm.RS256
import com.nimbusds.jose.jwk.source.RemoteJWKSet
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jose.util.DefaultResourceRetriever
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.BadJWTException
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.OrganizationValidator

class MaskinportenValidator(
    private val config: MaskinportenValidatorConfig
) {
    private val jwtProcessor = DefaultJWTProcessor<SecurityContext>().apply {
        jwsKeySelector =
            JWSVerificationKeySelector(
                RS256,
                RemoteJWKSet(config.jwksUrl, DefaultResourceRetriever().apply {
                    proxy = config.proxy
                })
            )
        jwtClaimsSetVerifier = DefaultJWTClaimsVerifier(
            JWTClaimsSet.Builder()
                .issuer(config.baseURL.host)
                .build(),
            setOf("client_id", "client_amr", ORGNO_CLAIM, "consumer", "exp", "iat", "jti")
        )
    }

    /**
     * @return Organization to whom the token belongs.
     */
    operator fun invoke(token: JWT, requiredScope: String) =
        jwtProcessor.process(token, null)
            .takeIf { requiredScope in it.getStringListClaim(SCOPE_CLAIM) }
            ?.getStringClaim(ORGNO_CLAIM)
            ?: throw BadJWTException("Token missing required scope.")

    operator fun <T> invoke(
        token: JWT, requiredScope: String,
        organizationValidator: OrganizationValidator<T>,
        o: T
    ) = organizationValidator(this(token, requiredScope), o)


    companion object {
        internal const val SCOPE_CLAIM = "scope"
        internal const val ORGNO_CLAIM = "client_orgno"
    }
}