package no.nav.pensjonsamhandling.maskinporten.validation.config

import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenValidatorHandlerInterceptor
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenValidatorInterceptorConfigurer
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator.NoopOrganisationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.pid.RequestAwarePidValidator
import no.nav.pensjonsamhandling.maskinporten.validation.pid.RequestAwarePidValidator.NoopPidValidator
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
    fun maskinportenValidator() = listOfNotNull(
        MaskinportenValidator(properties.environment, properties.proxy, properties.permitAll),
        if(properties.environment == Environment.Ver2) MaskinportenValidator(Environment.Dev, properties.proxy, properties.permitAll) else null
    )

    @Bean
    fun noopOrganisationValidator() = NoopOrganisationValidator()

    @Bean
    fun noopPidValidator() = NoopPidValidator()

    @Bean
    fun maskinportenValidatorHandlerInterceptor(
        maskinportenValidator: List<MaskinportenValidator>,
        organisationValidators: List<RequestAwareOrganisationValidator>,
        pidValidators: List<RequestAwarePidValidator>
    ) = MaskinportenValidatorHandlerInterceptor(maskinportenValidator, organisationValidators, pidValidators)

    @Bean
    fun maskinportenValidatorHandlerInterceptorConfigurer(maskinportenValidatorHandlerInterceptor: MaskinportenValidatorHandlerInterceptor) =
        MaskinportenValidatorInterceptorConfigurer(maskinportenValidatorHandlerInterceptor)
}
