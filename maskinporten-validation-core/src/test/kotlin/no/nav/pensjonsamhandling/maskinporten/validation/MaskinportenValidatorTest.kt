package no.nav.pensjonsamhandling.maskinporten.validation

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.nimbusds.jose.JWSAlgorithm.RS256
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jose.proc.BadJOSEException
import com.nimbusds.jose.shaded.json.JSONArray
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.nimbusds.jwt.proc.BadJWTException
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator.Companion.SCOPE_CLAIM
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.net.URL
import java.util.*

internal class MaskinportenValidatorTest {


    private val testConfig = MaskinportenValidatorConfig(URL("https://bogus.url"))
    private val validator = MaskinportenValidator(testConfig)


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

        private val jwsHeader: JWSHeader = JWSHeader.Builder(RS256)
            .jwk(jwk)
            .keyID(TEST_KEY_ID)
            .build()

        private val invalidJWSHeader: JWSHeader = JWSHeader.Builder(RS256)
            .jwk(invalidJwk)
            .keyID("bogus")
            .build()

        private val jwtClaimsSet: JWTClaimsSet = JWTClaimsSet.Builder()
            .issuer("localhost")
            .issueTime(Date())
            .expirationTime(Date(System.currentTimeMillis() + 300000L))
            .jwtID(null)
            .claim(SCOPE_CLAIM, JSONArray.toJSONString(listOf(TEST_SCOPE)))
            .claim("client_id", null)
            .claim("client_amr", null)
            .claim("consumer", null)
            .build()

        private val expiredClaimsSet: JWTClaimsSet = JWTClaimsSet.Builder(jwtClaimsSet)
            .expirationTime(Date())
            .build()

        val validJws = SignedJWT(jwsHeader, jwtClaimsSet)
        val expiredJws = SignedJWT(jwsHeader, expiredClaimsSet)
        val invalidJws = SignedJWT(invalidJWSHeader, jwtClaimsSet)

        private val mockServer = WireMockServer()

        @BeforeAll
        @JvmStatic
        fun setUp() {
            mockServer.stubFor(post("https://bogus.url/jwk").willReturn(okJson(jwks.toString())))
            mockServer.start()
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            mockServer.stop()
        }
    }
}