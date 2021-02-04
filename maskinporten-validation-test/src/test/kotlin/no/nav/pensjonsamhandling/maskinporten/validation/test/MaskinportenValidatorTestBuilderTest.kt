package no.nav.pensjonsamhandling.maskinporten.validation.test

import com.nimbusds.jose.proc.BadJOSEException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class MaskinportenValidatorTestBuilderTest {

    @Test
    fun `Test validator approves correct key`() {
        assertDoesNotThrow { builderA.getValidator().invoke(builderA.generateToken(SCOPE, ORGNO), SCOPE) }
    }

    @Test
    fun `Test validator rejects incorrect key`() {
        assertThrows<BadJOSEException> { builderA.getValidator().invoke(builderB.generateToken(SCOPE, ORGNO), SCOPE) }
    }

    companion object {
        private const val SCOPE = "testScope"
        private const val ORGNO = "testOrgno"
        private val builderA = MaskinportenValidatorTestBuilder("keyA")
        private val builderB = MaskinportenValidatorTestBuilder("keyB")

    }
}