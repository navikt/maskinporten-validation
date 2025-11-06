import com.nimbusds.jwt.JWT
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator

class ManualValidation(
    val validator: MaskinportenValidator
) {
    private val customOrgnoValidator = CustomOrgnoValidator()
    private val customPidValidator = CustomPidValidator()

    fun acceptAnyOrgno(token: JWT) = validator(token, "my:scope/here")

    fun acceptOnlyMyOrgnoAndPid(token: JWT) = validator(token, "my:scope/here", customOrgnoValidator, customPidValidator, null)
}
