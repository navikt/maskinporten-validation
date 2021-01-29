package no.nav.pensjonsamhandling.maskinporten.validation

import java.net.Proxy
import java.net.URL

data class MaskinportenValidatorConfig(
    internal val baseURL: URL,
    internal val proxy: Proxy? = null
) {
    internal val jwksUrl = URL(baseURL, "/jwk")
}
