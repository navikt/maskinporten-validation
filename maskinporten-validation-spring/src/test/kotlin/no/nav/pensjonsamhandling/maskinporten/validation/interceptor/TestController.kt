package no.nav.pensjonsamhandling.maskinporten.validation.interceptor

import no.nav.pensjonsamhandling.maskinporten.validation.annotation.Maskinporten
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenValidatorHandlerInterceptorTest.Companion.TEST_SCOPE
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator.DenyingOrganisationValidator
import org.springframework.http.HttpStatus.OK
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.server.ResponseStatusException

@Controller
@Maskinporten(TEST_SCOPE)
class TestController {

    @GetMapping("/bogus")
    fun acceptMapping() {
        throw ResponseStatusException(OK)
    }

    @Maskinporten(TEST_SCOPE, DenyingOrganisationValidator::class)
    @GetMapping("/deny")
    fun denyMapping() {
        throw ResponseStatusException(OK)
    }
}