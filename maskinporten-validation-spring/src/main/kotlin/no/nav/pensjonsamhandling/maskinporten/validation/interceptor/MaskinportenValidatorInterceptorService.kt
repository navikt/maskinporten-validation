package no.nav.pensjonsamhandling.maskinporten.validation.interceptor

import no.nav.pensjonsamhandling.maskinporten.validation.config.MaskinportenValidatorAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@Import(MaskinportenValidatorAutoConfiguration::class)
open class MaskinportenValidatorInterceptorService(
    private val maskinportenHandlerInterceptor: MaskinportenHandlerInterceptor
) : WebMvcConfigurer {

    @Override
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(maskinportenHandlerInterceptor)
    }
}