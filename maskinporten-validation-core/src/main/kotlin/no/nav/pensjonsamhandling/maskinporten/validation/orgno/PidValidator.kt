package no.nav.pensjonsamhandling.maskinporten.validation.orgno

@FunctionalInterface
interface PidValidator<T> {
    operator fun invoke(pid: String?, o: T): Boolean
}
