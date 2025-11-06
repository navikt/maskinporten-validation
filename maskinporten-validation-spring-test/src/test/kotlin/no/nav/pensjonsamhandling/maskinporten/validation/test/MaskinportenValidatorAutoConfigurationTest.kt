package no.nav.pensjonsamhandling.maskinporten.validation.test

import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator.DenyingOrganisationValidator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(classes = [TestController::class, DenyingOrganisationValidator::class])
@AutoConfigureMockMvc
@AutoConfigureWebMvc
@AutoConfigureMaskinportenValidator
internal class MaskinportenValidatorAutoConfigurationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var tokenGenerator: MaskinportenValidatorTokenGenerator

    @Test
    fun `Intercepts and accepts valid token`() {
        mockMvc.get("/bogus") {
            headers { setBearerAuth(tokenGenerator.generateToken(SCOPE, ORGNO).serialize()) }
        }.andExpect { status { isOk() } }
    }

    @Test
    fun `Intercepts and rejects invalid token`() {
        mockMvc.get("/deny") {
            headers { setBearerAuth(invalidBuilder.generateToken(SCOPE, ORGNO).serialize()) }
        }.andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `Permits permitall`() {
        mockMvc.get("/deny") {
            headers { setBearerAuth(tokenGenerator.generateToken(SCOPE, "1234567890").serialize()) }
        }.andExpect { status { isOk() } }
    }


    companion object {
        const val SCOPE = "testScope"
        const val ORGNO = "orgno"

        val invalidBuilder = MaskinportenValidatorTokenGenerator("invalid")
    }
}
