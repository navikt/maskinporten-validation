package no.nav.pensjonsamhandling.maskinporten.validation.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URL

@ConstructorBinding
@ConfigurationProperties("maskinporten.validation")
class MaskinportenValidatorProperties(
    environment: Environment.EnvType = Environment.EnvType.PROD,
    customUrl: String? = null,
    private val permitAll: List<String> = emptyList(),
    proxy: String? = null
) {
    val environment = when (environment) {
        Environment.EnvType.PROD -> Environment.Prod
        Environment.EnvType.DEV -> Environment.Dev
        Environment.EnvType.CUSTOM -> Environment.Custom(
            URL(
                customUrl
                    ?: throw InvalidConfigurationPropertyValueException(
                        "maskinporten.validation.customUrl",
                        customUrl,
                        "Must be specified when environment is set."
                    )
            )
        )
    }
    val proxy = proxy?.run {
        Proxy(
            Proxy.Type.HTTP,
            InetSocketAddress(
                substringBeforeLast(':'),
                substringAfterLast(':', "8080").toInt()
            )
        )
    }
}