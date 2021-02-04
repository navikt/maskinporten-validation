package no.nav.pensjonsamhandling.maskinporten.validation

fun String.postfix(postfix: String, ignoreCase: Boolean = false) =
    if (endsWith(postfix, ignoreCase)) this else this+postfix

fun String.postfix(postfix: Char, ignoreCase: Boolean = false) =
    postfix("$postfix", ignoreCase)