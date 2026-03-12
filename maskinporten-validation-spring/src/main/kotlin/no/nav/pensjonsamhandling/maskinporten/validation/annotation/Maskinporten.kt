package no.nav.pensjonsamhandling.maskinporten.validation.annotation

import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.orgno.RequestAwareOrganisationValidator.NoopOrganisationValidator
import no.nav.pensjonsamhandling.maskinporten.validation.pid.RequestAwarePidValidator
import no.nav.pensjonsamhandling.maskinporten.validation.pid.RequestAwarePidValidator.NoopPidValidator
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.reflect.KClass

@Retention(RUNTIME)
@Target(FUNCTION, CLASS)
annotation class Maskinporten(
    val scope: String,
    val orgValidatorClass: KClass<out RequestAwareOrganisationValidator> = NoopOrganisationValidator::class,
    val pidValidatorClass: KClass<out RequestAwarePidValidator> = NoopPidValidator::class,
    val consent: Consent = Consent("")
)
