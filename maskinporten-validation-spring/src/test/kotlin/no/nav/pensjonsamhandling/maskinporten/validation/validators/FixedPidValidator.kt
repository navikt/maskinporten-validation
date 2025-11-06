package no.nav.pensjonsamhandling.maskinporten.validation.validators

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjonsamhandling.maskinporten.validation.pid.RequestAwarePidValidator

class FixedPidValidator(val fixedPid: String?): RequestAwarePidValidator {
    override fun invoke(pid: String?, o: HttpServletRequest) = pid == fixedPid
}
