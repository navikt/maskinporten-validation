import no.nav.pensjonsamhandling.maskinporten.validation.consent.AuthorizationDetails
import no.nav.pensjonsamhandling.maskinporten.validation.consent.ConsentValidator

class CustomConsentValidator: ConsentValidator<Nothing?> {
    override fun invoke(
        consent: AuthorizationDetails?,
        o: Nothing?
    ) = consent?.from == "12345678910"
}
