package no.nav.pensjonsamhandling.maskinporten.validation.consent

@FunctionalInterface
interface ConsentValidator<T> {
    operator fun invoke(consent: AuthorizationDetails?, o: T): Boolean
}
