package no.nav.pensjonsamhandling.maskinporten.validation.annotation

import no.nav.pensjonsamhandling.maskinporten.validation.consent.RequestAwareConsentValidator
import no.nav.pensjonsamhandling.maskinporten.validation.consent.RequestAwareConsentValidator.NoopConsentValidator
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.reflect.KClass

@Retention(RUNTIME)
@Target(FUNCTION, CLASS)
annotation class Consent(
    val value: String,
    val consentValidatorClass: KClass<out RequestAwareConsentValidator> = NoopConsentValidator::class,
)
