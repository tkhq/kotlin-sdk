# com.turnkey.types

Strongly-typed data models for the Turnkey API.

Turnkey API docs: https://docs.turnkey.com

All types are code-generated from Turnkey's OpenAPI/Swagger and include:

* Enums with stable `@SerialName` mappings
* Request/response DTOs for all endpoints
* Embedded payload types

## Installation

Gradle (Kotlin DSL):
```kotlin
dependencies {
    implementation("com.turnkey:types:<version>")
    // required by the types:
    api("org.jetbrains.kotlinx:kotlinx-serialization-core:<serializationVersion>")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:<serializationVersion>")
}
```

> JVM + Android compatible.

## What's inside

* **Enums**: `V1ActivityStatus`, `V1AccessType`, etc. with OpenAPI values via `@SerialName`
* **Request/Response DTOs**: `V1OauthLoginRequest`, `V1OauthLoginResult`, etc.
* **Embedded types**: `V1Attestation`, `V1Authenticator`, etc.
* **Proxy types**: Auth Proxy models prefixed with `Proxy...`

Where to find things:

```
openapi/
    auth_proxy.swagger.json
    public_api.swagger.json
src/main/kotlin/
    com/turnkey/types/
        Models.kt
```

## Code generation

`Models.kt` is generated from OpenAPI specs.

To sync the types with updated OpenAPI specs, see the [**tools** package README](../tools/README.md).
