package no.nav.pensjonsamhandling.maskinporten.validation.orgno

import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

interface RequestAwareOrganizationValidator : OrganizationValidator<HttpServletRequest> {

    @Service
    class NoopOrganizationValidator : RequestAwareOrganizationValidator {
        override fun invoke(orgno: String, o: HttpServletRequest) = true
    }
}