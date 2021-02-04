package no.nav.pensjonsamhandling.maskinporten.validation.interceptor

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import no.nav.pensjonsamhandling.maskinporten.validation.DenyingOrganizationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator
import no.nav.pensjonsamhandling.maskinporten.validation.config.MaskinportenValidatorConfigurer
import no.nav.pensjonsamhandling.maskinporten.validation.config.MaskinportenValidatorConfig
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenHandlerInterceptorTest.TestConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.net.URL
import java.util.*

@WebMvcTest(TestController::class)
@ContextConfiguration(classes = [TestConfig::class, TestController::class, MaskinportenValidatorConfigurer::class])
internal class MaskinportenHandlerInterceptorTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `Intercepts and accepts valid token`() {
        mockMvc.get("/bogus") {
            headers { setBearerAuth(validJws.serialize()) }
        }.andExpect { status { isOk } }
    }

    @Test
    fun `Intercepts and rejects valid token with invalid orgno`() {
        mockMvc.get("/deny") {
            headers { setBearerAuth(validJws.serialize()) }
        }.andExpect { status { isUnauthorized } }

    }

    @TestConfiguration
    class TestConfig {
        @Bean
        @Primary
        fun testConfig() = testConfig

        @Bean
        fun denyingOrganizationValidator() = DenyingOrganizationValidator()
    }

    companion object {
        internal const val TEST_SCOPE = "nav:acceptedScope"
        private const val TEST_KEY_ID = "testId"
        private val jwk: RSAKey = RSAKeyGenerator(2048).keyID(TEST_KEY_ID).generate()
        private val jwks = JWKSet(jwk)

        val testConfig = MaskinportenValidatorConfig(URL("https://maskinporten.no/")).apply {
            jwkSet = ImmutableJWKSet(jwks)
        }

        private val jwsHeader: JWSHeader = JWSHeader.Builder(JWSAlgorithm.RS256)
            .jwk(jwk)
            .keyID(TEST_KEY_ID)
            .build()

        private val jwtClaimsSet: JWTClaimsSet = JWTClaimsSet.Builder()
            .issuer(testConfig.baseURL.toString())
            .issueTime(Date())
            .expirationTime(Date(System.currentTimeMillis() + 300000L))
            .jwtID("bogus")
            .claim(MaskinportenValidator.SCOPE_CLAIM, listOf(TEST_SCOPE))
            .claim("client_id", "bogus")
            .claim("client_amr", "bogus")
            .claim("consumer", "bogus")
            .claim(MaskinportenValidator.ORGNO_CLAIM, "bogus")
            .build()

        val validJws = SignedJWT(jwsHeader, jwtClaimsSet).apply {
            sign(DefaultJWSSignerFactory().createJWSSigner(jwk))
        }
    }
}