package no.nav.pensjonsamhandling.maskinporten.validation.config

import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenHandlerInterceptor
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganizationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganizationValidator.NoopOrganizationValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@EnableWebMvc
@Configuration
@Import(NoopOrganizationValidator::class)
open class MaskinportenValidatorAutoConfiguration(
    @Value("no.nav.pensjonsamhandling.maskinporten.validation")
    private val config: MaskinportenValidatorConfig,
    private val validators: List<RequestAwareOrganizationValidator>
) : WebMvcConfigurer {

    @Bean
    open fun maskinportenValidator() = MaskinportenValidator(config)

    @Bean
    open fun maskinportenHandlerInterceptor(maskinportenValidator: MaskinportenValidator): MaskinportenHandlerInterceptor =
        MaskinportenHandlerInterceptor(
            maskinportenValidator(),
            validators
        )
}
