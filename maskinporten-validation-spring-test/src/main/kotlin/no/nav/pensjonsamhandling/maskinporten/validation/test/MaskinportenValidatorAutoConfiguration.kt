package no.nav.pensjonsamhandling.maskinporten.validation.test

import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator
import no.nav.pensjonsamhandling.maskinporten.validation.config.MaskinportenValidatorProperties
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenValidatorHandlerInterceptor
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenValidatorInterceptorConfigurer
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator.NoopOrganisationValidator
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@AutoConfiguration
@EnableConfigurationProperties(MaskinportenValidatorProperties::class)
class MaskinportenValidatorAutoConfiguration(
    val config: MaskinportenValidatorProperties
) {
    @Bean
    @Primary
    fun tokenGenerator() = MaskinportenValidatorTokenGenerator(permitAll = config.permitAll)

    @Bean
    @Primary
    fun maskinportenTestValidator(tokenGenerator: MaskinportenValidatorTokenGenerator) = tokenGenerator.getValidator()

    @Bean
    @Primary
    fun noopOrganisationTestValidator() = NoopOrganisationValidator()

    @Bean
    @Primary
    fun maskinportenValidatorHandlerTestInterceptor(
        testValidator: List<MaskinportenValidator>,
        validators: List<RequestAwareOrganisationValidator>
    ) = MaskinportenValidatorHandlerInterceptor(testValidator, validators)

    @Bean
    @Primary
    fun maskinportenValidatorTestInterceptorConfigurer(maskinportenValidatorHandlerInterceptor: MaskinportenValidatorHandlerInterceptor) =
        MaskinportenValidatorInterceptorConfigurer(maskinportenValidatorHandlerInterceptor)
}