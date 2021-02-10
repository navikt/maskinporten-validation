package no.nav.pensjonsamhandling.maskinporten.validation.annotation

import no.nav.pensjonsamhandling.maskinporten.validation.config.MaskinportenValidatorConfigurer
import org.springframework.context.annotation.Import

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Import(MaskinportenValidatorConfigurer::class)
annotation class EnableMaskinportenValidation
