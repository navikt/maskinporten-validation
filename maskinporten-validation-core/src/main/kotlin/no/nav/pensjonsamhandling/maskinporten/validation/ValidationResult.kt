package no.nav.pensjonsamhandling.maskinporten.validation

class ValidationResult(
    val accepted: Boolean,
    val orgno: String,
    val pid: String?,
)
