package no.nav.pensjonsamhandling.maskinporten.validation.interceptor

import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

class MaskinportenValidatorInterceptorHandler(
    private val maskinportenValidatorHandlerInterceptor: MaskinportenValidatorHandlerInterceptor
) : WebMvcConfigurer {

    @Override
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(maskinportenValidatorHandlerInterceptor)
    }
}