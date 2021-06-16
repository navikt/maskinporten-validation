package no.nav.pensjonsamhandling.maskinporten.validation.test

import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenValidatorHandlerInterceptor
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenValidatorInterceptorHandler
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator.NoopOrganisationValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class MaskinportenValidatorAutoConfiguration {
    @Bean
    @Primary
    fun noopOrganisationValidator() = NoopOrganisationValidator()

    @Bean
    @Primary
    fun tokenGenerator() = MaskinportenValidatorTokenGenerator()

    @Bean
    @Primary
    fun testValidator(tokenGenerator: MaskinportenValidatorTokenGenerator) = tokenGenerator.getValidator()

    @Bean
    @Primary
    fun maskinportenHandlerInterceptor(
        testValidator: MaskinportenValidator,
        validators: List<RequestAwareOrganisationValidator>
    ): MaskinportenValidatorHandlerInterceptor =
        MaskinportenValidatorHandlerInterceptor(
            testValidator,
            validators
        )

    @Bean
    @Primary
    fun maskinportenValidatorInterceptorHandler(maskinportenValidatorHandlerInterceptor: MaskinportenValidatorHandlerInterceptor) =
        MaskinportenValidatorInterceptorHandler(maskinportenValidatorHandlerInterceptor)
}