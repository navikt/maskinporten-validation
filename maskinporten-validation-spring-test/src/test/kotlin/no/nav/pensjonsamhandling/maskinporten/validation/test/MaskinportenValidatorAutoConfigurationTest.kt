package no.nav.pensjonsamhandling.maskinporten.validation.test

import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganizationValidator.DenyingOrganizationValidator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(classes = [TestController::class, DenyingOrganizationValidator::class])
@AutoConfigureMockMvc
@AutoConfigureWebMvc
@AutoconfigureMaskinportenValidator
internal class MaskinportenValidatorAutoConfigurationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var testBuilder: MaskinportenValidatorTestBuilder

    @Test
    fun `Intercepts and accepts valid token`() {
        mockMvc.get("/bogus") {
            headers { setBearerAuth(testBuilder.generateToken(SCOPE, ORGNO).serialize()) }
        }.andExpect { status { isOk } }
    }

    @Test
    fun `Intercepts and rejects invalid token`() {
        mockMvc.get("/deny") {
            headers { setBearerAuth(invalidBuilder.generateToken(SCOPE, ORGNO).serialize()) }
        }.andExpect { status { isUnauthorized } }
    }

    companion object {
        const val SCOPE = "testScope"
        const val ORGNO = "orgno"

        val invalidBuilder = MaskinportenValidatorTestBuilder("invalid")
    }
}