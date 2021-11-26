import no.nav.pensjonsamhandling.maskinporten.validation.orgno.OrganisationValidator

class CustomValidator: OrganisationValidator<Nothing?> {
    override fun invoke(orgno: String, o: Nothing?) = orgno == "12345678910"
}