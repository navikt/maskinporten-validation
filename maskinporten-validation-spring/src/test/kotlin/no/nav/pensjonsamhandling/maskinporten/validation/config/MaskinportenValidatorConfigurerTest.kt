package no.nav.pensjonsamhandling.maskinporten.validation.config

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

@SpringBootTest(classes = [MaskinportenValidatorConfigurer::class, DenyingOrganisationValidator::class])
internal class MaskinportenValidatorConfigurerTest {

    @Autowired
    lateinit var config: MaskinportenValidatorConfig

    @Autowired
    lateinit var validators: List<RequestAwareOrganisationValidator>

    @Test
    fun `Spring autoconfigures MaskinportenConfig`(){
        assertEquals(expectedConfig, config)
    }

    @Test
    fun `Spring configures custom organisation validators`(){
        assert(validators.any { it is NoopOrganisationValidator })
        assert(validators.any { it is DenyingOrganisationValidator })
    }

    companion object {
        private val expectedConfig = MaskinportenValidatorConfig(
            URL("https://maskinporten.no/"),
            Proxy(HTTP, InetSocketAddress("localhost", 8080))
        )
    }
}