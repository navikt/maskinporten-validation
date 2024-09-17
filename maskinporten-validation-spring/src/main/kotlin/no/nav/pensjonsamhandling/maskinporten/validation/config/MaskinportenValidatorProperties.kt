package no.nav.pensjonsamhandling.maskinporten.validation.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URI

@ConfigurationProperties("maskinporten.validation")
class MaskinportenValidatorProperties(
    environment: Environment.EnvType = Environment.EnvType.PROD,
    customUrl: String? = null,
    val permitAll: List<String> = emptyList(),
    proxy: String? = null
) {
    val environment = when (environment) {
        Environment.EnvType.PROD -> Environment.Prod
        Environment.EnvType.VER2 -> Environment.Ver2
        Environment.EnvType.DEV -> Environment.Dev
        Environment.EnvType.CUSTOM -> Environment.Custom(
            URI(
                customUrl
                    ?: throw InvalidConfigurationPropertyValueException(
                        "maskinporten.validation.customUrl",
                        customUrl,
                        "Must be specified when environment is set."
                    )
            ).toURL()
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