package no.nav.pensjonsamhandling.maskinporten.validation.config

import no.nav.pensjonsamhandling.maskinporten.validation.annotation.EnableMaskinportenValidation
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator.DenyingOrganisationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator.NoopOrganisationValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.Proxy.Type.HTTP
import java.net.URL

@SpringBootTest(classes = [DenyingOrganisationValidator::class])
@EnableMaskinportenValidation
internal class MaskinportenValidatorConfigurerTest {

    @Autowired
    lateinit var maskinportenValidatorConfig: MaskinportenValidatorConfig

    @Autowired
    lateinit var validators: List<RequestAwareOrganisationValidator>

    @Test
    fun `Spring autoconfigures MaskinportenConfig`(){
        assertEquals(expectedConfig, maskinportenValidatorConfig)
    }

    @Test
    fun `Spring configures custom organisation validators`(){
        assert(validators.any { it is NoopOrganisationValidator })
        assert(validators.any { it is DenyingOrganisationValidator })
    }

    companion object {
        private val expectedConfig = MaskinportenValidatorConfig(
            URL("https://maskinporten.no/"),
            listOf("00000000"),
            Proxy(HTTP, InetSocketAddress("localhost", 8080))
        )
    }
}