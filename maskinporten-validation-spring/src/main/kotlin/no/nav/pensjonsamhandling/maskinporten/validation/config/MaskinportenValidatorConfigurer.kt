package no.nav.pensjonsamhandling.maskinporten.validation.config

import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenHandlerInterceptor
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenValidatorInterceptorHandler
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganizationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganizationValidator.NoopOrganizationValidator
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@EnableWebMvc
@Configuration
@EnableConfigurationProperties(MaskinportenValidatorProperties::class)
class MaskinportenValidatorConfigurer(
    internal val properties: MaskinportenValidatorProperties,
) : WebMvcConfigurer {

    @Bean
    fun noopOrganizationValidator() = NoopOrganizationValidator()

    @Bean
    fun config() = properties.toConfig()

    @Bean
    fun maskinportenValidator(config: MaskinportenValidatorConfig) = MaskinportenValidator(config)

    @Bean
    fun maskinportenHandlerInterceptor(
        maskinportenValidator: MaskinportenValidator,
        validators: List<RequestAwareOrganizationValidator>
    ): MaskinportenHandlerInterceptor =
        MaskinportenHandlerInterceptor(
            maskinportenValidator,
            validators
        )

    @Bean
    fun maskinportenValidatorInterceptorHandler(maskinportenHandlerInterceptor: MaskinportenHandlerInterceptor) =
        MaskinportenValidatorInterceptorHandler(maskinportenHandlerInterceptor)
}
