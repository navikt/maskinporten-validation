# maskinporten-validation-test
Test library containing support for maskinporten-validation-core.

## Classes

### MaskinportenValidatorTestBuilder
Builder class for generating a local MaskinportenValidator and valid keys.

Member | Description
---|---
keyId: String (optional) | key_id to use for signing JWK.
getValidator() | Returns a MaskinportenValidator that validates tokens against a local JWKSet.
generateToken( <br> scope: String, <br> orgno: String, <br> clientId: String (optional), <br> clientAmr: String (optional), <br> consumer: String (optional) <br> ) | Generate a valid token with the supplied claims. <br><br> scope - The scope claim of the generated token. <br> orgno - The organisation identifier claim of the generated token. <br> clientId - The client_id claim of the generated token. <br> clientAmr - The client_amr claim of the generated token. <br> consumer - The consumer claim of the generated token.