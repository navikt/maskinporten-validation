package no.nav.pensjonsamhandling.maskinporten.annotation

import no.nav.pensjonsamhandling.maskinporten.orgno.RequestAwareOrganizationValidator
import no.nav.pensjonsamhandling.maskinporten.orgno.RequestAwareOrganizationValidator.NoopOrganizationValidator
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.TYPE
import kotlin.reflect.KClass

@Retention(RUNTIME)
@Target(FUNCTION, TYPE)
annotation class Maskinporten(
    val scope: String,
    val orgValidator: KClass<out RequestAwareOrganizationValidator> = NoopOrganizationValidator::class
)