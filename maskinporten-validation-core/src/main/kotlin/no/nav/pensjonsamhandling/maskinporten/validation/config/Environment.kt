package no.nav.pensjonsamhandling.maskinporten.validation.config

import java.net.URI
import java.net.URL

sealed class Environment(val baseURL: URL) {
    enum class EnvType {
        PROD,
        VER2,
        DEV,
        CUSTOM
    }
    object Prod: Environment(URI.create("https://maskinporten.no/").toURL())
    object Ver2: Environment(URI.create("https://ver2.maskinporten.no/").toURL())
    object Dev: Environment(URI.create("https://test.maskinporten.no/").toURL())
    class Custom(baseUrl: URL): Environment(baseUrl)
}