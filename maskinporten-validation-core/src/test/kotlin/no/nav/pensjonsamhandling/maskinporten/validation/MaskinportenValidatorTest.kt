package no.nav.pensjonsamhandling.maskinporten.validation

import com.nimbusds.jose.JWSAlgorithm.RS256
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.BadJOSEException
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.nimbusds.jwt.proc.BadJWTException
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator.Companion.ORGNO_CLAIM
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator.Companion.SCOPE_CLAIM
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.net.URL
import java.time.Instant
import java.util.*

internal class MaskinportenValidatorTest {

    @Test
    fun `Accepts valid token`() {
        assertDoesNotThrow { validator(validJws, TEST_SCOPE) }
    }

    @Test
    fun `Rejects expired token`() {
        assertThrows(BadJOSEException::class.java) { validator(expiredJws, TEST_SCOPE)}
    }

    @Test
    fun `Rejects invalid token`() {
        assertThrows(BadJOSEException::class.java) { validator(invalidJws, TEST_SCOPE)}
    }

    @Test
    fun `Rejects token missing required scope`() {
        assertThrows(BadJWTException::class.java) { validator(validJws, "bogus")}
    }

    companion object{
        private const val TEST_SCOPE = "test"
        private const val TEST_KEY_ID = "testId"

        private val jwk: RSAKey = RSAKeyGenerator(2048).keyID(TEST_KEY_ID).generate()
        private val invalidJwk: RSAKey = RSAKeyGenerator(2048).keyID("bogus").generate()
        private val jwks = JWKSet(jwk)

        private val testConfig = MaskinportenValidatorConfig(URL("https://maskinporten.no/")).apply {
            jwkSet = ImmutableJWKSet(jwks)
        }
        private val validator = MaskinportenValidator(testConfig)

        private val jwsHeader: JWSHeader = JWSHeader.Builder(RS256)
            .jwk(jwk)
            .keyID(TEST_KEY_ID)
            .build()

        private val invalidJWSHeader: JWSHeader = JWSHeader.Builder(RS256)
            .jwk(invalidJwk)
            .keyID("bogus")
            .build()

        private val jwtClaimsSet: JWTClaimsSet = JWTClaimsSet.Builder()
            .issuer(testConfig.baseURL.toString())
            .issueTime(Date())
            .expirationTime(Date(System.currentTimeMillis() + 300000L))
            .jwtID(null)
            .claim(SCOPE_CLAIM, listOf(TEST_SCOPE))
            .claim("client_id", null)
            .claim("client_amr", null)
            .claim("consumer", null)
            .claim(ORGNO_CLAIM, "bogus")
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
}