package no.nav.pensjonsamhandling.maskinporten.validation.config

import java.net.URL

sealed class Environment(val baseURL: URL) {
    enum class EnvType {
        PROD,
        DEV,
        CUSTOM
    }
    object Prod: Environment(URL("https://maskinporten.no/"))
    object Dev: Environment(URL("https://ver2.maskinporten.no/"))
    class Custom(baseUrl: URL): Environment(baseUrl)
}