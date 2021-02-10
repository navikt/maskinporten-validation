package no.nav.pensjonsamhandling.maskinporten.validation.config

import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenValidatorHandlerInterceptor
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenValidatorInterceptorHandler
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator.NoopOrganisationValidator
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
    fun noopOrganisationValidator() = NoopOrganisationValidator()

    @Bean
    fun maskinportenValidatorConfig() = properties.toConfig()

    @Bean
    fun maskinportenValidator(config: MaskinportenValidatorConfig) = MaskinportenValidator(config)

    @Bean
    fun maskinportenHandlerInterceptor(
        maskinportenValidator: MaskinportenValidator,
        validators: List<RequestAwareOrganisationValidator>,
    ): MaskinportenValidatorHandlerInterceptor =
        MaskinportenValidatorHandlerInterceptor(
            maskinportenValidator,
            validators
        )

    @Bean
    fun maskinportenValidatorInterceptorHandler(maskinportenValidatorHandlerInterceptor: MaskinportenValidatorHandlerInterceptor) =
        MaskinportenValidatorInterceptorHandler(maskinportenValidatorHandlerInterceptor)
}
