package no.nav.pensjonsamhandling.maskinporten.validation.consent

import com.nimbusds.jose.shaded.gson.JsonObject

class ConsentResource(
    val type: String,
    val value: String
) {
    constructor(json: JsonObject) : this(
        json["type"].asString,
        json["value"].asString
    )
}
