package no.nav.pensjonsamhandling.maskinporten.validation.validators

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator

class FixedOrgnoValidator(val fixedOrgno: String): RequestAwareOrganisationValidator {
    override fun invoke(orgno: String, o: HttpServletRequest) = orgno == fixedOrgno
}
