# maskinporten-validation-core
This is the core library. It is manually configured. You probably don't want to use this directly unless you are using your own implementation of endpoint security.

## Classes

### Environment
Sealed class used to determine which instance of Maskinporten to use.

| Member | Description                                                                 |
|--------|-----------------------------------------------------------------------------|
| Prod   | (Static) Production environment. `https://maskinporten.no/`                 |
| Dev    | (Static) Development environment. `https://test.maskinporten.no/`           |
| Custom | Custom environment for integration testing. Takes a user-specified baseURL. |

### OrganisationValidator
Interface for additional access control.

| Member                               | Description                                                                                                                                                            |
|--------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| invoke(orgno: String, o: T): Boolean | Should return whether the organisation has permission to perform the requested operation. <br><br> orgno - The organisation identifier. <br> o - Additional parameter. |

### PidValidator
Interface for additional access control.

| Member                              | Description                                                                                                                                                    |
|-------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| invoke(pid: String?, o: T): Boolean | Should return whether the requested operation is valid for the pid supplied in the token. <br><br> pid - The person identifier. <br> o - Additional parameter. |

### ConsentValidator
Interface for additional access control.

| Member                                                | Description                                                                                                                                                                                                  |
|-------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| invoke(consent: AuthorizationDetails?, o: T): Boolean | Should return whether the requested operation is valid for the consent supplied in the token. <br><br> consent - Consent detailed in the token's authorization_details claim. <br> o - Additional parameter. |

### MaskinportenValidator
Class responsible for validating tokens.

| Member                                                                                                                                                                   | Description                                                                                                                                                                                                                                                                                                                                                  |
|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| environment: Environment                                                                                                                                                 | The environment to use for validation.                                                                                                                                                                                                                                                                                                                       |
| proxy: Proxy (optional)                                                                                                                                                  | The proxy to use in order to reach Maskinporten. If none is supplied, the JVM default proxy will be used.                                                                                                                                                                                                                                                    |
| permitAll: List<String>                                                                                                                                                  | OrgNos permitted to access all resources. Bypass orgno validation.                                                                                                                                                                                                                                                                                           |
| requirePid: Boolean                                                                                                                                                      | Require pid claim in all tokens.                                                                                                                                                                                                                                                                                                                             |
| requireConsent: Boolean                                                                                                                                                  | Require authorization_details claim in all tokens.                                                                                                                                                                                                                                                                                                           |
| invoke(token: JWT, requiredScope: String): Boolean                                                                                                                       | Returns whether the token is valid and contains the required scope. <br> token - The token to validate. <br> requiredScope - The scope required to access the endpoint.                                                                                                                                                                                      |
| invoke( <br> token: JWT, <br> requiredScope: String, <br> organisationValidator: OrganisationValidator<T>, <br> pidValidator: PidValidator<T>, <br> o: T <br> ): Boolean | Returns whether the token is valid and contains the required scope, and the organisation is accepted by the OrganisationValidator. <br><br> organisationValidator - The validator to use for validating the organisation number <br> pidValidator - The validator to use for validating the pid <br> o - Additional parameter for the organisationValidator. |

### ValidationResult
Class containing the result of the token validation.

| Member            | Description                                                 |
|-------------------|-------------------------------------------------------------|
| accepted: Boolean | Whether the token was successfully validated.               |
| orgnr: String     | The organisation identifier supplied in the consumer claim. |
| pid: String?      | The personal identifier supplied in the pid claim.          |
