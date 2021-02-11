package no.nav.pensjonsamhandling.maskinporten.validation.test

import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*

@Target(FUNCTION, CLASS)
@Retention(RUNTIME)
@Inherited
@Import(MaskinportenValidatorAutoConfiguration::class)
annotation class AutoConfigureMaskinportenValidator
