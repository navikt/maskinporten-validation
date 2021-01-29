package no.nav.pensjonsamhandling.maskinporten.orgno

import no.nav.pensjonsamhandling.maskinporten.validation.orgno.OrganizationValidator
import javax.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service

@Service
interface RequestAwareOrganizationValidator: OrganizationValidator<HttpServletRequest> {
    class NoopOrganizationValidator: RequestAwareOrganizationValidator{
        override fun invoke(orgno: String, o: HttpServletRequest) = true
    }
}