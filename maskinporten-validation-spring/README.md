# maskinporten-validation-spring
Library for automatically handling the configuration of your validator and attempts to validate incoming requests on any class or method annotated with `@Maskinporten`. To enable autoconfiguration, annotate your application class or a config class with `@EnableMaskinportenValidation`.

## Configuration
Key | Description
---|---
maskinporten.validation.baseURL | The base URL of the Maskinporten implementation issuing the tokens. (For production, this will be `https://maskinporten.no/`)
maskinporten.validation.permitAll | List of orgnos permitted access to all resources. Bypasses orgno validation.
maskinporten.validation.proxy (optional) | The proxy URL to use in order to reach Maskinporten. If none is supplied, the JVM default proxy will be used.

## Classes

### RequestAwareOrganisationValidator
Interface for additional access control using the request as parameter.

Member | Description
---|---
invoke(orgno: String, o: HttpServletRequest): Boolean | Should return whether the organisation has permission to perform the requested operation. <br><br> orgno - The organisation identifier. <br> o - The request that triggered the validation attempt.

### @Maskinporten
This annotation marks a class or method on which to intercept incomming requests and attempt to validate the bearer token.

Member | Description
---|---
scope: String | The scope required to access the annotated resource(s).
orgValidator: KClass<out RequestAwareOrganisationValidator> (optional) | OrganisationValidator implementation to use for additionall access control.