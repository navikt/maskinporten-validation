package no.nav.pensjonsamhandling.maskinporten.validation.config

import no.nav.pensjonsamhandling.maskinporten.validation.annotation.EnableMaskinportenValidation
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator.DenyingOrganisationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator.NoopOrganisationValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.Proxy.Type.HTTP
import java.net.URL

@ActiveProfiles("custom")
@SpringBootTest(classes = [DenyingOrganisationValidator::class])
@EnableMaskinportenValidation
@EnableConfigurationProperties(MaskinportenValidatorProperties::class)
internal class MaskinportenValidatorConfigurerCustomEnvTest {

    @Autowired
    lateinit var properties: MaskinportenValidatorProperties

    @Autowired
    lateinit var validators: List<RequestAwareOrganisationValidator>

    @Test
    fun `Spring autoconfigures MaskinportenConfig`(){
        assertEquals(expectedCustomUrl, properties.environment.baseURL)
        assertEquals(expectedProxy, properties.proxy)
    }

    @Test
    fun `Spring configures custom organisation validators`(){
        assert(validators.any { it is NoopOrganisationValidator })
        assert(validators.any { it is DenyingOrganisationValidator })
    }

    companion object {
        private val expectedCustomUrl = URL("https://localhost")
        private val expectedProxy = Proxy(HTTP, InetSocketAddress("localhost", 8080))
    }
}