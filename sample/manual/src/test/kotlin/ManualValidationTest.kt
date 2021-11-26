import com.nimbusds.jose.proc.BadJOSEException
import no.nav.pensjonsamhandling.maskinporten.validation.test.MaskinportenValidatorTokenGenerator
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

internal class ManualValidationTest {
    val tokenGenerator = MaskinportenValidatorTokenGenerator()
    val otherGenerator = MaskinportenValidatorTokenGenerator()
    val app = ManualValidation(tokenGenerator.getValidator())

    @Test
    fun `Accept any Orgno`() {
        assertDoesNotThrow { app.acceptAnyOrgno(tokenGenerator.generateToken("my:scope/here", "")) }
    }

    @Test
    fun `Deny incorrect scope`() {
        assertThrows<BadJOSEException> { app.acceptAnyOrgno(tokenGenerator.generateToken("not:my/scope", "12345678910")) }
    }

    @Test
    fun `Deny incorrect provider`() {
        assertThrows<BadJOSEException> { app.acceptAnyOrgno(otherGenerator.generateToken("my:scope/here", "12345678910")) }
    }

    @Test
    fun `Accept only my Orgno`() {
        assertTrue(app.acceptOnlyMyOrgno(tokenGenerator.generateToken("my:scope/here", "12345678910")))
    }

    @Test
    fun `Deny wrong Orgno`() {
        assertFalse(app.acceptOnlyMyOrgno(tokenGenerator.generateToken("my:scope/here", "10987654321")))
    }
}