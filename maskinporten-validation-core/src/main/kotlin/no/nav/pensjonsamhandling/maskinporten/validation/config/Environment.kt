package no.nav.pensjonsamhandling.maskinporten.validation.config

import java.net.URL

sealed class Environment(val baseURL: URL) {
    enum class EnvType {
        PROD,
        VER2,
        DEV,
        CUSTOM
    }
    object Prod: Environment(URL("https://maskinporten.no/"))
    object Ver2: Environment(URL("https://ver2.maskinporten.no/"))
    object Dev: Environment(URL("https://test.maskinporten.no/"))
    class Custom(baseUrl: URL): Environment(baseUrl)
}