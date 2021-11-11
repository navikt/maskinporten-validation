package no.nav.pensjonsamhandling.maskinporten.validation.config

import no.nav.pensjonsamhandling.maskinporten.validation.postfix
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URL

@ConstructorBinding
@ConfigurationProperties("maskinporten.validation")
class MaskinportenValidatorProperties(
    private val baseURL: String,
    private val permitAll: List<String> = emptyList(),
    private var proxy: String? = null
) {

    fun toConfig() = MaskinportenValidatorConfig(
        URL(baseURL.postfix('/')),
        permitAll,
        proxy?.run {
            Proxy(
                Proxy.Type.HTTP,
                InetSocketAddress(
                    substringBeforeLast(':'),
                    substringAfterLast(':', "8080").toInt()
                )
            )
        })
}