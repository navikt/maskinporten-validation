package no.nav.pensjonsamhandling.maskinporten.validation.orgno

@FunctionalInterface
interface OrganizationValidator<T> {
    operator fun invoke(orgno: String, o: T): Boolean
}