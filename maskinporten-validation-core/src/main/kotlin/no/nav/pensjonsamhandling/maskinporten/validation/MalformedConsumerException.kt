package no.nav.pensjonsamhandling.maskinporten.validation

class MalformedConsumerException(consumer: String): Exception("Malformed consumer claim: $consumer") {
}
