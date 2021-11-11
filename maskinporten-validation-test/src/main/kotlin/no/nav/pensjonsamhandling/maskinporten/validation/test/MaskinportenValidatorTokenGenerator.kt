package no.nav.pensjonsamhandling.maskinporten.validation.test

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.shaded.json.JSONObject
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator.Companion.CONSUMER_CLAIM
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator.Companion.SCOPE_CLAIM
import no.nav.pensjonsamhandling.maskinporten.validation.config.MaskinportenValidatorConfig
import java.util.*

class MaskinportenValidatorTokenGenerator(
    val config: MaskinportenValidatorConfig,
    keyId: String = "keyId"
) {

    private val jwk: RSAKey = RSAKeyGenerator(2048).keyID(keyId).generate()
    private val jwks: JWKSet = JWKSet(jwk)

    init {
        config.jwkSet = ImmutableJWKSet(jwks)
    }

    fun getValidator() = MaskinportenValidator(config)

    private val jwsHeader: JWSHeader = JWSHeader.Builder(JWSAlgorithm.RS256)
        .jwk(jwk)
        .keyID(keyId)
        .build()

    private fun getConsumer(orgno: String) = JSONObject().apply {
        set("authority", "iso6523-actorid-upis")
        set("ID", "0192:$orgno")
    }

    private fun getJwtClaimsSet(
        scope: String,
        orgno: String,
        clientId: String,
        clientAmr: String,
        consumer: String,
    ): JWTClaimsSet = JWTClaimsSet.Builder()
        .issuer(config.baseURL.toString())
        .issueTime(Date())
        .expirationTime(Date(System.currentTimeMillis() + 300000L))
        .jwtID("jti")
        .claim(SCOPE_CLAIM, listOf(scope))
        .claim("client_id", clientId)
        .claim("client_amr", clientAmr)
        .claim("consumer", consumer)
        .claim(CONSUMER_CLAIM, getConsumer(orgno))
        .build()

    fun generateToken(
        scope: String,
        orgno: String,
        clientId: String = "client_id",
        clientAmr: String = "client_amr",
        consumer: String = "consumer"
    ) = SignedJWT(jwsHeader, getJwtClaimsSet(scope, orgno, clientId, clientAmr, consumer)).apply {
        sign(DefaultJWSSignerFactory().createJWSSigner(jwk))
    }
}