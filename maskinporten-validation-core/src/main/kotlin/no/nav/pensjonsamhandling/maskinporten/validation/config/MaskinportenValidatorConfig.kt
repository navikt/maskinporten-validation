package no.nav.pensjonsamhandling.maskinporten.validation.config

import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.jwk.source.RemoteJWKSet
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jose.util.DefaultResourceRetriever
import java.net.Proxy
import java.net.URL

data class MaskinportenValidatorConfig(
    val baseURL: URL,
    val jwkURL: URL? = null,
    val issuer: String? = null,
    internal val proxy: Proxy? = null
) {
    var jwkSet: JWKSource<SecurityContext> = RemoteJWKSet(
        jwkURL ?: URL(baseURL, "/jwk"),
        DefaultResourceRetriever().apply {
            proxy = this@MaskinportenValidatorConfig.proxy
        })
}
