package no.nav.pensjonsamhandling.maskinporten.validation.consent

import com.nimbusds.jose.shaded.gson.JsonObject
import com.nimbusds.jose.shaded.gson.internal.bind.util.ISO8601Utils
import java.text.ParsePosition
import java.util.*

class AuthorizationDetails(
    val from: String,
    val to: ConsentRecipient,
    val consented: Date,
    val validTo: Date,
    val consentRights: Collection<ConsentRight>
) {
    constructor(json: JsonObject): this(
        json["from"].asString,
        ConsentRecipient(json["to"].asJsonObject),
        ISO8601Utils.parse(json["consented"].asString, ParsePosition(0)),
        ISO8601Utils.parse(json["validTo"].asString, ParsePosition(0)),
        json["consentRights"].asJsonArray.map {
            ConsentRight(it.asJsonObject)
        }
    )

    val pid: String
        get() = from.substringAfterLast(':')
    fun has(consent: String) = consentRights.any { it.has(consent) }
    operator fun get(consent: String) = consentRights.firstOrNull { it.has(consent) }
}
