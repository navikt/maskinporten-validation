package no.nav.pensjonsamhandling.maskinporten.validation.config

import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.jwk.source.RemoteJWKSet
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jose.util.DefaultResourceRetriever
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.Proxy.Type.HTTP
import java.net.URL

data class MaskinportenValidatorConfig(
    internal val baseURL: URL,
    internal val proxy: Proxy? = null
) {
    constructor(
        baseURL: String,
        proxy: String?
    ) : this(URL(baseURL), proxy?.run {
        Proxy(
            HTTP,
            InetSocketAddress(
                substringBeforeLast(':'),
                substringAfterLast(':', "8080").toInt()
            )
        )
    })

    internal var jwkSet: JWKSource<SecurityContext> = RemoteJWKSet(
        URL(baseURL, "/jwk"),
        DefaultResourceRetriever().apply {
            proxy = proxy
        })
}
