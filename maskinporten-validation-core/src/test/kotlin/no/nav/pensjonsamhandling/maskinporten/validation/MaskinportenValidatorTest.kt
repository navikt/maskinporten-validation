package no.nav.pensjonsamhandling.maskinporten.validation

import com.nimbusds.jose.proc.BadJOSEException
import com.nimbusds.jwt.proc.BadJWTException
import no.nav.pensjonsamhandling.maskinporten.validation.config.MaskinportenValidatorTestConfig.TEST_SCOPE
import no.nav.pensjonsamhandling.maskinporten.validation.config.MaskinportenValidatorTestConfig.expiredJws
import no.nav.pensjonsamhandling.maskinporten.validation.config.MaskinportenValidatorTestConfig.invalidJws
import no.nav.pensjonsamhandling.maskinporten.validation.config.MaskinportenValidatorTestConfig.testJWKSet
import no.nav.pensjonsamhandling.maskinporten.validation.config.MaskinportenValidatorTestConfig.validJws
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class MaskinportenValidatorTest {

    private val validator = MaskinportenValidator().apply {
        jwkSet = testJWKSet
    }

    @Test
    fun `Accepts valid token`() {
        assertDoesNotThrow { validator(validJws, TEST_SCOPE) }
    }

    @Test
    fun `Rejects expired token`() {
        assertThrows(BadJOSEException::class.java) { validator(expiredJws, TEST_SCOPE) }
    }

    @Test
    fun `Rejects invalid token`() {
        assertThrows(BadJOSEException::class.java) { validator(invalidJws, TEST_SCOPE) }
    }

    @Test
    fun `Rejects token missing required scope`() {
        assertThrows(BadJWTException::class.java) { validator(validJws, "bogus") }
    }
}