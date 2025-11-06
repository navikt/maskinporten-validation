# maskinporten-validation-spring
Library for automatically handling the configuration of your validator and attempts to validate incoming requests on any class or method annotated with `@Maskinporten`. To enable autoconfiguration, annotate your application class or a config class with `@EnableMaskinportenValidation`.

If a token is successfully validated, the `orgno` and `pid` request attributes are set according to the claims supplied in the token, and may be bound in your controller using the Spring `@RequestAttribute` annotation.

## Configuration
| Key                                            | Description                                                                                                                           |
|------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| maskinporten.validation.environment (optional) | The environment to use for determining URLs. May be PROD, DEV or CUSTOM. Default: PROD.                                               |
| maskinporten.validation.customUrl (optional)   | Base URL to use when environment is CUSTOM. Required if environment is CUSTOM, otherwise ignored. Example: `https://maskinporten.no/` |
| maskinporten.validation.permitAll              | List of orgnos permitted access to all resources. Bypasses orgno validation.                                                          |
| maskinporten.validation.requirePid (optional)  | Whether to require a pid claim in all received Maskinporten tokens. Default: False.                                                   |
| maskinporten.validation.proxy (optional)       | The proxy URL to use in order to reach Maskinporten. If none is supplied, the JVM default proxy will be used.                         |

## Classes

### RequestAwareOrganisationValidator
Interface for additional access control using the request as parameter.

| Member                                                | Description                                                                                                                                                                                         |
|-------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| invoke(orgno: String, o: HttpServletRequest): Boolean | Should return whether the organisation has permission to perform the requested operation. <br><br> orgno - The organisation identifier. <br> o - The request that triggered the validation attempt. |

### @Maskinporten
This annotation marks a class or method on which to intercept incomming requests and attempt to validate the bearer token.

| Member                                                                      | Description                                                                |
|-----------------------------------------------------------------------------|----------------------------------------------------------------------------|
| scope: String                                                               | The scope required to access the annotated resource(s).                    |
| orgValidatorClass: KClass<out RequestAwareOrganisationValidator> (optional) | OrganisationValidator implementation to use for additional access control. |
| pidValidatorClass: KClass<out RequestAwarePidValidator> (optional)          | PidValidator implementation to use for additional access control.          |
