package no.nav.pensjonsamhandling.maskinporten.validation.test

import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*

@Target(TYPE, FUNCTION, CLASS)
@Retention(RUNTIME)
@Inherited
@ImportAutoConfiguration
annotation class AutoconfigureMaskinportenValidator
