package no.nav.pensjonsamhandling.maskinporten.validation.consent

import jakarta.servlet.http.HttpServletRequest

interface RequestAwareConsentValidator : ConsentValidator<HttpServletRequest> {
    class NoopConsentValidator : RequestAwareConsentValidator {
        override fun invoke(consent: AuthorizationDetails?, o: HttpServletRequest) = true
    }
    class DenyingConsentValidator : RequestAwareConsentValidator {
        override fun invoke(consent: AuthorizationDetails?, o: HttpServletRequest) = false
    }
}
