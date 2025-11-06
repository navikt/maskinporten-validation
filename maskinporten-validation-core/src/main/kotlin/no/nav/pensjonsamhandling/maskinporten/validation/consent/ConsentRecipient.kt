package no.nav.pensjonsamhandling.maskinporten.validation.consent

import com.nimbusds.jose.shaded.gson.JsonObject

class ConsentRecipient(
    val id: String
) {
    constructor(json: JsonObject) : this(json["id"].asString)
    val orgno: String
        get() = id.substringAfterLast(':')
}
