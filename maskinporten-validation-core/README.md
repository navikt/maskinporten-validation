# maskinporten-validation-core
This is the core library. It is manually configured. You probably don't want to use this directly unless you are using your own implementation of endpoint security.

## Classes

### MaskinportenValidatorConfig
Config class containing network parameters.

Member | Description
---|---
baseUrl: URL | The base URL of the Maskinporten implementation issuing the tokens. (For production, this will be `https://maskinporten.no/`)
permitAll: List<String> | OrgNos permitted to access all resources. Bypass orgno validation.
proxy: Proxy (optional) | The proxy to use in order to reach Maskinporten. If none is supplied, the JVM default proxy will be used.

### OrganisationValidator
Interface for additional access control.

Member | Description
---|---
invoke(orgno: String, o: T): Boolean | Should return whether the organisation has permission to perform the requested operation. <br><br> orgno - The organisation identifier. <br> o - Additional parameter.

### MaskinportenValidator
Class responsible for validating tokens.

Member | Description
---|---
maskinportenValidatorConfig: MaskinportenValidatorConfig | The config supplying the network parameters.
invoke(token: JWT, requiredScope: String): Boolean | Returns whether the token is valid and contains the required scope. <br> token - The token to validate. <br> requiredScope - The scope required to access the endpoint.
invoke( <br> token: JWT, <br> requiredScope: String, <br> organisationValidator: OrganisationValidator<T>, <br> o: T <br> ): Boolean | Returns whether the token is valid and contains the required scope, and the organisation is accepted by the OrganisationValidator. <br><br> organisationValidator - The validator to use for validating the organisation number <br> o - Additional parameter for the organisationValidator.