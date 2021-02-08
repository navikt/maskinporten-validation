package no.nav.pensjonsamhandling.maskinporten.validation.test

import no.nav.pensjonsamhandling.maskinporten.validation.annotation.Maskinporten
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator.DenyingOrganisationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.test.MaskinportenValidatorAutoConfigurationTest.Companion.SCOPE
import org.springframework.http.HttpStatus.OK
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.server.ResponseStatusException

@Controller
@Maskinporten(SCOPE)
class TestController {

    @GetMapping("/bogus")
    fun acceptMapping() {
        throw ResponseStatusException(OK)
    }

    @Maskinporten(SCOPE, DenyingOrganisationValidator::class)
    @GetMapping("/deny")
    fun denyMapping() {
        throw ResponseStatusException(OK)
    }
}