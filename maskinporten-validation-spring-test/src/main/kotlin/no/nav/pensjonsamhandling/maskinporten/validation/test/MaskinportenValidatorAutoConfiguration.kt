package no.nav.pensjonsamhandling.maskinporten.validation.test

import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenValidatorHandlerInterceptor
import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenValidatorInterceptorHandler
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganizationValidator
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@AutoConfigureAfter(AutoconfigureMaskinportenValidator::class)
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
    @ConditionalOnMissingBean(MaskinportenValidatorHandlerInterceptor::class)
    fun maskinportenHandlerInterceptor(
        maskinportenValidator: MaskinportenValidator,
        validators: List<RequestAwareOrganizationValidator>
    ): MaskinportenValidatorHandlerInterceptor =
        MaskinportenValidatorHandlerInterceptor(
            maskinportenValidator,
            validators
        )

    @Bean
    @ConditionalOnMissingBean(MaskinportenValidatorInterceptorHandler::class)
    fun maskinportenValidatorInterceptorHandler(maskinportenValidatorHandlerInterceptor: MaskinportenValidatorHandlerInterceptor) =
        MaskinportenValidatorInterceptorHandler(maskinportenValidatorHandlerInterceptor)
}