import com.nimbusds.jwt.JWT
import no.nav.pensjonsamhandling.maskinporten.validation.MaskinportenValidator

class ManualValidation(
    val validator: MaskinportenValidator
) {
    private val customOrgnoValidator = CustomOrgnoValidator()
    private val customPidValidator = CustomPidValidator()
    private val customConsentValidator = CustomConsentValidator()

    fun acceptAnyOrgno(token: JWT) = validator(token, "my:scope/here")

    fun acceptOnlyMyOrgnoAndPid(token: JWT) = validator(
        token = token,
        requiredScope = "my:scope/here",
        requiredConsent = "my-consent-here",
        organisationValidator = customOrgnoValidator,
        pidValidator = customPidValidator,
        consentValidator = customConsentValidator,
        o = null
    )
}
