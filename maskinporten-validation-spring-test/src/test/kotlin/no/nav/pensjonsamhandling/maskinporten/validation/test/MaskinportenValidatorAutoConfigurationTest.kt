package no.nav.pensjonsamhandling.maskinporten.validation.test

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(TestController::class)
@ContextConfiguration(classes = [TestController::class])
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