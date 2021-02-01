package no.nav.pensjonsamhandling.maskinporten.validation

import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.jwk.source.RemoteJWKSet
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jose.util.DefaultResourceRetriever
import java.net.Proxy
import java.net.URL

data class MaskinportenValidatorConfig(
    internal val baseURL: URL,
    internal val proxy: Proxy? = null
) {
    internal var jwkSet: JWKSource<SecurityContext> = RemoteJWKSet(
        URL(baseURL, "/jwk"),
        DefaultResourceRetriever().apply {
            proxy = proxy
        })
}
