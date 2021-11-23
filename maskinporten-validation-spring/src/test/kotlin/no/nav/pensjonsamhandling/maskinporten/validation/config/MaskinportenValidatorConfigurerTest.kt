package no.nav.pensjonsamhandling.maskinporten.validation.config

import no.nav.pensjonsamhandling.maskinporten.validation.annotation.EnableMaskinportenValidation
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator.DenyingOrganisationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator.NoopOrganisationValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.Proxy.Type.HTTP

@SpringBootTest(classes = [DenyingOrganisationValidator::class])
@EnableMaskinportenValidation
@EnableConfigurationProperties(MaskinportenValidatorProperties::class)
internal class MaskinportenValidatorConfigurerTest {

    @Autowired
    lateinit var properties: MaskinportenValidatorProperties

    @Autowired
    lateinit var validators: List<RequestAwareOrganisationValidator>

    @Test
    fun `Spring autoconfigures MaskinportenConfig`(){
        assertEquals(expectedEnvironment, properties.environment)
        assertTrue(properties.permitAll.containsAll(expectedPermitAll))
        assertEquals(expectedProxy, properties.proxy)
    }

    @Test
    fun `Spring configures custom organisation validators`(){
        assert(validators.any { it is NoopOrganisationValidator })
        assert(validators.any { it is DenyingOrganisationValidator })
    }

    companion object {
        private val expectedEnvironment = Environment.Prod
        private val expectedPermitAll = listOf("00000000")
        private val expectedProxy = Proxy(HTTP, InetSocketAddress("localhost", 8080))
    }
}