# maskinporten-validation
This is a series of libraries for validating tokens issued by [Maskinporten](https://docs.digdir.no/maskinporten_index.html).

## maskinporten-validation-core
The core library. It is manually configured. You probably don't want to use this directly unless you are using your own implementation of endpoint security.

### Classes

#### MaskinportenValidatorConfig
Config class containing network parameters.

Member | Description
---|---
baseUrl: URL | The base URL of the Maskinporten implementation issuing the tokens. (For production, this will be `https://maskinporten.no/`)
proxy: Proxy (optional) | The proxy to use in order to reach Maskinporten. If none is supplied, the JVM default proxy will be used.

#### OrganisationValidator
Interface for additional access control.

Member | Description
---|---
invoke(orgno: String, o: T): Boolean | Should return whether the organisation has permission to perform the requested operation. <br><br> orgno - The organisation identifier. <br> o - Additional parameter.

#### MaskinportenValidator
Class responsible for validating tokens.

Member | Description
---|---
maskinportenValidatorConfig: MaskinportenValidatorConfig | The config supplying the network parameters.
invoke(token: JWT, requiredScope: String): Boolean | Returns whether the token is valid and contains the required scope. <br> token - The token to validate. <br> requiredScope - The scope required to access the endpoint.
invoke(token: JWT, requiredScope: String, organisationValidator: OrganisationValidator<T>, o: T): Boolean | Returns whether the token is valid and contains the required scope, and the organisation is accepted by the OrganisationValidator. <br><br> organisationValidator - The validator to use for validating the organisation number <br> o - Additional parameter for the organisationValidator.

---

## maskinporten-validation-spring
Library for automatically handling the configuration of your validator and attempts to validate incoming requests on any class or method annotated with `@Maskinporten`. To enable autoconfiguration, annotate your application class or a config class with `@EnableMaskinportenValidation`.

### Properties
Key | Description
---|---
maskinporten.validation.baseURL | The base URL of the Maskinporten implementation issuing the tokens. (For production, this will be `https://maskinporten.no/`)
maskinporten.validation.proxy (optional) | The proxy URL to use in order to reach Maskinporten. If none is supplied, the JVM default proxy will be used.

### Classes

#### RequestAwareOrganisationValidator
Interface for additional access control using the request as parameter.

Member | Description
---|---
invoke(orgno: String, o: HttpServletRequest): Boolean | Should return whether the organisation has permission to perform the requested operation. <br><br> orgno - The organisation identifier. <br> o - The request that triggered the validation attempt.

#### @Maskinporten
This annotation marks a class or method on which to intercept incomming requests and attempt to validate the bearer token.

Member | Description
---|---
scope: String | The scope required to access the annotated resource(s).
orgValidator: KClass<out RequestAwareOrganisationValidator> (optional) | OrganisationValidator implementation to use for additionall access control.

---

## maskinporten-validation-test
Test library containing support for maskinporten-validation-core.

### Classes

#### MaskinportenValidatorTestBuilder
Builder class for generating a local MaskinportenValidator and valid keys.

Member | Description
---|---
keyId: String (optional) | key_id to use for signing JWK.
getValidator() | Returns a MaskinportenValidator that validates tokens against a local JWKSet.
generateToken( <br> scope: String, <br> orgno: String, <br> clientId: String (optional), <br> clientAmr: String (optional), <br> consumer: String (optional) <br> ) | Generate a valid token with the supplied claims. <br><br> scope - The scope claim of the generated token. <br> orgno - The organisation identifier claim of the generated token. <br> clientId - The client_id claim of the generated token. <br> clientAmr - The client_amr claim of the generated token. <br> consumer - The consumer claim of the generated token.

---

## maskinporten-validation-spring-test
Test library containing support for maskinporten-validation-spring.

Simply annotate your Spring test class with `@AutoConfigureMaskinportenValidator` and this library handles the rest. Supplies a MaskinportenValidatorTestBuilder bean which you can autowire in order to generate tokens.

---

For questions, contact [Mathias Sand Jahren](https://teamkatalog.nais.adeo.no/resource/J156788?source=slackprofile).
