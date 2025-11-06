package no.nav.pensjonsamhandling.maskinporten.validation.interceptor

import no.nav.pensjonsamhandling.maskinporten.validation.annotation.Maskinporten
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenValidatorHandlerInterceptorTest.Companion.TEST_SCOPE
import no.nav.pensjonsamhandling.maskinporten.validation.validators.FixedOrgnoValidator
import no.nav.pensjonsamhandling.maskinporten.validation.validators.FixedPidValidator
import org.springframework.http.HttpStatus.OK
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.server.ResponseStatusException

@Controller
@Maskinporten(TEST_SCOPE, FixedOrgnoValidator::class, FixedPidValidator::class)
class TestController {

    @GetMapping("/bogus")
    fun acceptMapping(
        @RequestAttribute pid: String,
        @RequestAttribute orgno: String,
    ) {
        throw ResponseStatusException(OK)
    }
}
