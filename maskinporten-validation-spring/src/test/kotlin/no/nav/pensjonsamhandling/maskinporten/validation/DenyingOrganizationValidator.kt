package no.nav.pensjonsamhandling.maskinporten.validation

import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganizationValidator
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class DenyingOrganizationValidator : RequestAwareOrganizationValidator {
    override fun invoke(orgno: String, o: HttpServletRequest) = false
}