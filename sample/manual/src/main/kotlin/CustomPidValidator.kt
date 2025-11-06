import no.nav.pensjonsamhandling.maskinporten.validation.orgno.PidValidator

class CustomPidValidator: PidValidator<Nothing?> {
    override fun invoke(pid: String?, o: Nothing?) = pid == "12345678910"
}
