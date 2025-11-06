package no.nav.pensjonsamhandling.maskinporten.validation.interceptor

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator.Companion.CONSUMER_CLAIM
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator.Companion.PID_CLAIM
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator.Companion.SCOPE_CLAIM
import no.nav.pensjonsamhandling.maskinporten.validation.config.Environment
import no.nav.pensjonsamhandling.maskinporten.validation.config.MaskinportenValidatorConfigurer
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenValidatorHandlerInterceptorTest.TestConfig
import no.nav.pensjonsamhandling.maskinporten.validation.validators.FixedOrgnoValidator
import no.nav.pensjonsamhandling.maskinporten.validation.validators.FixedPidValidator
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.util.*

@WebMvcTest(TestController::class)
@ContextConfiguration(classes = [TestConfig::class, TestController::class, MaskinportenValidatorConfigurer::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MaskinportenValidatorHandlerInterceptorTest {

    @Autowired
    private lateinit var maskinportenValidator: List<MaskinportenValidator>

    @Autowired
    private lateinit var mockMvc: MockMvc

    @BeforeAll
    fun init() {
        maskinportenValidator.first().jwkSet = testJWKSet
    }

    @Test
    fun `Intercepts and accepts valid token`() {
        mockMvc.get("/bogus") {
            headers { setBearerAuth(validJws.serialize()) }
        }.andExpect { status { isOk() } }
    }

    @Test
    fun `Intercepts and rejects valid token with invalid orgno`() {
        mockMvc.get("/bogus") {
            headers { setBearerAuth(wrongOrgnoJws.serialize()) }
        }.andExpect { status { isForbidden() } }
    }

    @Test
    fun `Intercepts and rejects valid token with invalid pid`() {
        mockMvc.get("/bogus") {
            headers { setBearerAuth(wrongPidJws.serialize()) }
        }.andExpect { status { isForbidden() } }
    }

    @Test
    fun `Intercepts and rejects valid token with missing pid`() {
        mockMvc.get("/bogus") {
            headers { setBearerAuth(missingPidJws.serialize()) }
        }.andExpect { status { isForbidden() } }
    }

    @TestConfiguration
    class TestConfig {
        @Bean
        fun fixedOrgnoValidator() = FixedOrgnoValidator(VALID_ORGNO)
        @Bean
        fun fixedPidValidator() = FixedPidValidator(VALID_PID)
    }

    companion object {
        internal const val TEST_SCOPE = "nav:acceptedScope"
        private const val TEST_KEY_ID = "testId"
        private const val VALID_ORGNO = "12345678"
        private const val VALID_PID = "12345678910"
        private val jwk: RSAKey = RSAKeyGenerator(2048).keyID(TEST_KEY_ID).generate()
        private val jwks = JWKSet(jwk)

        val testJWKSet: JWKSource<SecurityContext> = ImmutableJWKSet(jwks)

        private val jwsHeader: JWSHeader = JWSHeader.Builder(JWSAlgorithm.RS256)
            .jwk(jwk.toPublicJWK())
            .keyID(TEST_KEY_ID)
            .build()

        private fun getConsumer(@Suppress("SameParameterValue") orgno: String) = mapOf(
            "authority" to "iso6523-actorid-upis",
            "ID" to "0192:$orgno"
        )

        private fun jwtClaimsSet(orgno: String = VALID_ORGNO, pid: String? = VALID_PID): JWTClaimsSet = JWTClaimsSet.Builder()
            .issuer(Environment.Prod.baseURL.toString())
            .issueTime(Date())
            .expirationTime(Date(System.currentTimeMillis() + 300000L))
            .jwtID("bogus")
            .claim(SCOPE_CLAIM, listOf(TEST_SCOPE))
            .claim("client_id", "bogus")
            .claim("client_amr", "bogus")
            .claim(CONSUMER_CLAIM, getConsumer(orgno))
            .run {
                if (pid != null) claim(PID_CLAIM, pid) else this
            }
            .build()

        val validJws = SignedJWT(jwsHeader, jwtClaimsSet()).apply {
            sign(DefaultJWSSignerFactory().createJWSSigner(jwk))
        }

        val wrongOrgnoJws = SignedJWT(jwsHeader, jwtClaimsSet(orgno = "87654321")).apply {
            sign(DefaultJWSSignerFactory().createJWSSigner(jwk))
        }

        val wrongPidJws = SignedJWT(jwsHeader, jwtClaimsSet(pid = "10987654321")).apply {
            sign(DefaultJWSSignerFactory().createJWSSigner(jwk))
        }

        val missingPidJws = SignedJWT(jwsHeader, jwtClaimsSet(pid = null)).apply {
            sign(DefaultJWSSignerFactory().createJWSSigner(jwk))
        }
    }
}
