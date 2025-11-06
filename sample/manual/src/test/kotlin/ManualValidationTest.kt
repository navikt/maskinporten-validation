import com.nimbusds.jose.proc.BadJOSEException
import no.nav.pensjonsamhandling.maskinporten.validation.MissingConsentException
import no.nav.pensjonsamhandling.maskinporten.validation.MissingScopeException
import no.nav.pensjonsamhandling.maskinporten.validation.test.MaskinportenValidatorTokenGenerator
import no.nav.pensjonsamhandling.maskinporten.validation.test.MaskinportenValidatorTokenGenerator.Consent
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
        assertThrows<MissingScopeException> { app.acceptAnyOrgno(tokenGenerator.generateToken("not:my/scope", "12345678910")) }
    }

    @Test
    fun `Deny incorrect provider`() {
        assertThrows<BadJOSEException> { app.acceptAnyOrgno(otherGenerator.generateToken("my:scope/here", "12345678910")) }
    }

    @Test
    fun `Accept only my Orgno and PID`() {
        assertTrue(app.acceptOnlyMyOrgnoAndPid(tokenGenerator.generateToken(
            scope = "my:scope/here",
            orgno = "12345678",
            pid = "12345678910",
            consent = Consent(
                type = "my-consent-here",
                from = "12345678910",
                to = "12345678",
            )
        )).accepted)
    }

    @Test
    fun `Deny missing consent`() {
        assertThrows<MissingConsentException> {
            app.acceptOnlyMyOrgnoAndPid(tokenGenerator.generateToken(
                scope = "my:scope/here",
                orgno = "12345678",
                pid = "12345678910",
                consent = Consent(
                    type = "wrong consent",
                    from = "12345678910",
                    to = "12345678",
                )
            )).accepted
        }
    }

    @Test
    fun `Deny consent for wrong orgno`() {
        assertThrows<MissingConsentException> {
            app.acceptOnlyMyOrgnoAndPid(tokenGenerator.generateToken(
                scope = "my:scope/here",
                orgno = "12345678",
                pid = "12345678910",
                consent = Consent(
                    type = "wrong consent",
                    from = "12345678910",
                    to = "87654321",
                )
            )).accepted
        }
    }

    @Test
    fun `Deny wrong Orgno`() {
        assertFalse(app.acceptOnlyMyOrgnoAndPid(tokenGenerator.generateToken(
            scope = "my:scope/here",
            orgno = "87654321",
            pid = "12345678910",
            consent = Consent(
                type = "my-consent-here",
                from = "12345678910",
                to = "87654321",
            )
        )).accepted)
    }

    @Test
    fun `Deny wrong PID`() {
        assertFalse(app.acceptOnlyMyOrgnoAndPid(tokenGenerator.generateToken(
            scope = "my:scope/here",
            orgno = "12345678",
            pid = "01987654321",
            consent = Consent(
                type = "my-consent-here",
                from = "12345678910",
                to = "12345678",
            )
        )).accepted)
    }
}
