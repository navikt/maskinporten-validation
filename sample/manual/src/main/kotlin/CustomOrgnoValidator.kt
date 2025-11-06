import no.nav.pensjonsamhandling.maskinporten.validation.orgno.OrganisationValidator

class CustomOrgnoValidator: OrganisationValidator<Nothing?> {
    override fun invoke(orgno: String, o: Nothing?) = orgno == "12345678"
}
