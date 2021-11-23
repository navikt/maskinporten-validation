package no.nav.pensjonsamhandling.maskinporten.validation.config

import java.net.Proxy

data class MaskinportenValidatorConfig(
    val environment: Environment = Environment.Prod,
    val permitAll: List<String> = emptyList(),
    internal val proxy: Proxy? = null
)
