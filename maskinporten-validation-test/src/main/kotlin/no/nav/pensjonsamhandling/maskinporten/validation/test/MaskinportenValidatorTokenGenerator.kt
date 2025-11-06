package no.nav.pensjonsamhandling.maskinporten.validation.test

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator.Companion.CONSUMER_CLAIM
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator.Companion.PID_CLAIM
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator.Companion.SCOPE_CLAIM
import no.nav.pensjonsamhandling.maskinporten.validation.config.Environment
import java.util.*

class MaskinportenValidatorTokenGenerator(
    keyId: String = "keyId",
    private val permitAll: List<String> = emptyList()
) {

    private val jwk: RSAKey = RSAKeyGenerator(2048).keyID(keyId).generate()
    private val jwks: JWKSet = JWKSet(jwk)

    fun getValidator() = MaskinportenValidator(permitAll = permitAll).apply {
        jwkSet = ImmutableJWKSet(jwks)
    }

    private val jwsHeader: JWSHeader = JWSHeader.Builder(JWSAlgorithm.RS256)
        .jwk(jwk.toPublicJWK())
        .keyID(keyId)
        .build()

    private fun getConsumer(orgno: String) = mapOf(
        "authority" to "iso6523-actorid-upis",
        "ID" to "0192:$orgno"
    )

    private fun getJwtClaimsSet(
        scope: String,
        orgno: String,
        clientId: String,
        clientAmr: String,
        consumer: String,
        pid: String?,
    ): JWTClaimsSet = JWTClaimsSet.Builder()
        .issuer(Environment.Prod.baseURL.toString())
        .issueTime(Date())
        .expirationTime(Date(System.currentTimeMillis() + 300000L))
        .jwtID("jti")
        .claim(SCOPE_CLAIM, listOf(scope))
        .claim("client_id", clientId)
        .claim("client_amr", clientAmr)
        .claim("consumer", consumer)
        .claim(CONSUMER_CLAIM, getConsumer(orgno))
        .let {
            if (pid != null) it.claim(PID_CLAIM, pid) else it
        }
        .build()

    fun generateToken(
        scope: String,
        orgno: String,
        clientId: String = "client_id",
        clientAmr: String = "client_amr",
        consumer: String = "consumer",
        pid: String? = null,
    ) = SignedJWT(jwsHeader, getJwtClaimsSet(scope, orgno, clientId, clientAmr, consumer, pid)).apply {
        sign(DefaultJWSSignerFactory().createJWSSigner(jwk))
    }
}
