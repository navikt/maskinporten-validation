package no.nav.pensjonsamhandling.maskinporten.validation.orgno

import jakarta.servlet.http.HttpServletRequest

interface RequestAwareOrganisationValidator : OrganisationValidator<HttpServletRequest> {
    class NoopOrganisationValidator : RequestAwareOrganisationValidator {
        override fun invoke(orgno: String, o: HttpServletRequest) = true
    }
    class DenyingOrganisationValidator : RequestAwareOrganisationValidator {
        override fun invoke(orgno: String, o: HttpServletRequest) = false
    }
}