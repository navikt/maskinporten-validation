package no.nav.pensjonsamhandling.maskinporten.validation.config

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import net.minidev.json.JSONObject
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator.Companion.CONSUMER_CLAIM
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator.Companion.SCOPE_CLAIM
import java.net.URL
import java.time.Instant
import java.util.*

object MaskinportenValidatorTestConfig {
    const val TEST_SCOPE = "test"
    private const val TEST_KEY_ID = "testId"

    private val jwk: RSAKey = RSAKeyGenerator(2048).keyID(TEST_KEY_ID).generate()
    private val invalidJwk: RSAKey = RSAKeyGenerator(2048).keyID("bogus").generate()
    private val jwks = JWKSet(jwk)

    val testConfig = MaskinportenValidatorConfig(URL("https://maskinporten.no/")).apply {
        jwkSet = ImmutableJWKSet(jwks)
    }

    private val jwsHeader: JWSHeader = JWSHeader.Builder(JWSAlgorithm.RS256)
        .jwk(jwk)
        .keyID(TEST_KEY_ID)
        .build()

    private val invalidJWSHeader: JWSHeader = JWSHeader.Builder(JWSAlgorithm.RS256)
        .jwk(invalidJwk)
        .keyID("bogus")
        .build()

    private fun getConsumer(orgno: String) = JSONObject().apply {
        set("authority", "iso6523-actorid-upis")
        set("ID", "0192:$orgno")
    }

    private val jwtClaimsSet: JWTClaimsSet = JWTClaimsSet.Builder()
        .issuer(testConfig.baseURL.toString())
        .issueTime(Date())
        .expirationTime(Date(System.currentTimeMillis() + 300000L))
        .jwtID(null)
        .claim(SCOPE_CLAIM, listOf(TEST_SCOPE))
        .claim("client_id", null)
        .claim("client_amr", null)
        .claim("consumer", null)
        .claim(CONSUMER_CLAIM, getConsumer("bogus"))
        .build()

    private val expiredClaimsSet: JWTClaimsSet = JWTClaimsSet.Builder(jwtClaimsSet)
        .expirationTime(Date.from(Instant.now().minusSeconds(60)))
        .build()

    val validJws = SignedJWT(jwsHeader, jwtClaimsSet).apply {
        sign(DefaultJWSSignerFactory().createJWSSigner(jwk))
    }
    val expiredJws = SignedJWT(jwsHeader, expiredClaimsSet).apply {
        sign(DefaultJWSSignerFactory().createJWSSigner(jwk))
    }
    val invalidJws = SignedJWT(invalidJWSHeader, jwtClaimsSet).apply {
        sign(DefaultJWSSignerFactory().createJWSSigner(invalidJwk))
    }
}