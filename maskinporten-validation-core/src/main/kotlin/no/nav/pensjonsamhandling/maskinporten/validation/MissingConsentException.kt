package no.nav.pensjonsamhandling.maskinporten.validation

class MissingConsentException(consent: String): Exception("Missing consent: $consent") {
}
