package no.nav.pensjonsamhandling.maskinporten.validation.consent

import com.nimbusds.jose.shaded.gson.JsonObject

class ConsentRight(
    val action: Collection<String>,
    val resource: Collection<ConsentResource>,
    val metadata: Map<String, String>
) {
    constructor(json: JsonObject) : this(
        json["action"].asJsonArray.map { it.asString },
        json["resource"].asJsonArray.map { ConsentResource(it.asJsonObject) },
        json["metadata"].asJsonObject.asMap().mapValues { (_, value) -> value.asString }
    )

    fun has(consent: String) = resource.any { it.value == consent }
}
