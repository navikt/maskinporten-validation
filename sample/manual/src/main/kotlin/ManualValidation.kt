import com.nimbusds.jwt.JWT
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator

class ManualValidation(
    val validator: MaskinportenValidator
) {
    private val customValidator = CustomValidator()

    fun acceptAnyOrgno(token: JWT) = validator(token, "my:scope/here")

    fun acceptOnlyMyOrgno(token: JWT) = validator(token, "my:scope/here", customValidator, null)
}