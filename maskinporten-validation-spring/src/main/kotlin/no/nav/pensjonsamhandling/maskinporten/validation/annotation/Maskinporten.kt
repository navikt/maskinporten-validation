package no.nav.pensjonsamhandling.maskinporten.validation.annotation

import no.nav.pensjonsamhandling.maskinporten.validation.interceptor.MaskinportenValidatorInterceptorService
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganizationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganizationValidator.NoopOrganizationValidator
import org.springframework.context.annotation.Import
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.TYPE
import kotlin.reflect.KClass

@Retention(RUNTIME)
@Target(FUNCTION, TYPE)
@Import(MaskinportenValidatorInterceptorService::class)
annotation class Maskinporten(
    val scope: String,
    val orgValidator: KClass<out RequestAwareOrganizationValidator> = NoopOrganizationValidator::class
)