package no.nav.pensjonsamhandling.maskinporten.validation.orgno

@FunctionalInterface
interface OrganisationValidator<T> {
    operator fun invoke(orgno: String, o: T): Boolean
}