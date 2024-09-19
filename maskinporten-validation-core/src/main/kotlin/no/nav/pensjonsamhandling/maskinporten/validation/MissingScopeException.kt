package no.nav.pensjonsamhandling.maskinporten.validation

class MissingScopeException(scope: String): Exception("Missing scope: $scope") {
}