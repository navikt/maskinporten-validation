package no.nav.pensjonsamhandling.maskinporten.validation.config

import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenValidatorHandlerInterceptor
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenValidatorInterceptorConfigurer
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator.NoopOrganisationValidator
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@EnableWebMvc
@Configuration
@EnableConfigurationProperties(MaskinportenValidatorProperties::class)
@ConditionalOnMissingClass("no.nav.pensjonsamhandling.maskinporten.validation.test.MaskinportenValidatorAutoConfiguration")
class MaskinportenValidatorConfigurer(
    internal val properties: MaskinportenValidatorProperties,
) : WebMvcConfigurer {
    @Bean
    fun maskinportenValidator() = MaskinportenValidator(properties.environment, properties.proxy, properties.permitAll)

    @Bean
    fun noopOrganisationValidator() = NoopOrganisationValidator()

    @Bean
    fun maskinportenValidatorHandlerInterceptor(
        maskinportenValidator: MaskinportenValidator,
        validators: List<RequestAwareOrganisationValidator>,
    ) = MaskinportenValidatorHandlerInterceptor(maskinportenValidator, validators)

    @Bean
    fun maskinportenValidatorHandlerInterceptorConfigurer(maskinportenValidatorHandlerInterceptor: MaskinportenValidatorHandlerInterceptor) =
        MaskinportenValidatorInterceptorConfigurer(maskinportenValidatorHandlerInterceptor)
}
