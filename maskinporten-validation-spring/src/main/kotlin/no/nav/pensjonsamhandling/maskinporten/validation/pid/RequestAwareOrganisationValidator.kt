package no.nav.pensjonsamhandling.maskinporten.validation.pid

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.PidValidator

interface RequestAwarePidValidator : PidValidator<HttpServletRequest> {
    class NoopPidValidator : RequestAwarePidValidator {
        override fun invoke(pid: String?, o: HttpServletRequest) = true
    }
    class DenyingPidValidator : RequestAwarePidValidator {
        override fun invoke(pid: String?, o: HttpServletRequest) = false
    }
}
