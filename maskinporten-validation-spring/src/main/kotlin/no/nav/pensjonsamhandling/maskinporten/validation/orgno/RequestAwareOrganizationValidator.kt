package no.nav.pensjonsamhandling.maskinporten.validation.orgno

import javax.servlet.http.HttpServletRequest

interface RequestAwareOrganizationValidator : OrganizationValidator<HttpServletRequest> {
    class NoopOrganizationValidator : RequestAwareOrganizationValidator {
        override fun invoke(orgno: String, o: HttpServletRequest) = true
    }
    class DenyingOrganizationValidator : RequestAwareOrganizationValidator {
        override fun invoke(orgno: String, o: HttpServletRequest) = false
    }
}