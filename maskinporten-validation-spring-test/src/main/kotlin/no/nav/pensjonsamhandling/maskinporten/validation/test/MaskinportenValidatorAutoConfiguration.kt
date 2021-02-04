package no.nav.pensjonsamhandling.maskinporten.validation.test

import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenValidatorHandlerInterceptor
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenValidatorInterceptorHandler
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganizationValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class MaskinportenValidatorAutoConfiguration {
    @Bean
    fun noopOrganizationValidator() = RequestAwareOrganizationValidator.NoopOrganizationValidator()

    @Bean
    @Primary
    fun testConfig(testBuilder: MaskinportenValidatorTestBuilder) = testBuilder.getValidator()

    @Bean
    @Primary
    fun testValidator() = MaskinportenValidatorTestBuilder()

    @Bean
    @Primary
    fun maskinportenHandlerInterceptor(
        maskinportenValidator: MaskinportenValidator,
        validators: List<RequestAwareOrganizationValidator>
    ): MaskinportenValidatorHandlerInterceptor =
        MaskinportenValidatorHandlerInterceptor(
            maskinportenValidator,
            validators
        )

    @Bean
    @Primary
    fun maskinportenValidatorInterceptorHandler(maskinportenValidatorHandlerInterceptor: MaskinportenValidatorHandlerInterceptor) =
        MaskinportenValidatorInterceptorHandler(maskinportenValidatorHandlerInterceptor)
}