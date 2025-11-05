# com.turnkey.http

A lower-level, fully typed HTTP client for interacting with the Turnkey API.

Turnkey API docs: https://docs.turnkey.com

The client is code-generated from Turnkey’s OpenAPI/Swagger and exposes:

* A TurnkeyClient with one method per endpoint
* Strongly typed request/response models

## Installation

Gradle (Kotlin DSL):
```kotlin
dependencies {
    implementation("com.turnkey:http:<version>")
    // required by the client:
    implementation("com.squareup.okhttp3:okhttp:<okhttpVersion>")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:<serializationVersion>")
    // your chosen stamper implementation (see below)
    implementation("com.turnkey:stamper:<version>")
}
```

> JVM + Android compatible.

## Quick start

### 1) Create the client

```kotlin
val client = TurnkeyClient(
    apiBaseUrl = "https://api.turnkey.com", // defaults if null
    stamper = Stamper(apiPublicKey = "...", apiPrivateKey = "..."),
    authProxyUrl = "https://authproxy.turnkey.com",
    authProxyConfigId = null // populate if auth proxy usage is planned
)
```
> Constructor defaults (if you pass `null`):
> `apiBaseUrl = "https://api.turnkey.com"`
> `authProxyUrl = "https://authproxy.turnkey.com"`

### 2) Make a typed request

```kotlin
val res = client.getWhoami(
        TGetWhoamiBody(organizationId = "<your-org-id>")
    )
println("whoami: $res")
```

All endpoints follow the same pattern: a `client.<method>(input = ...)` call with strongly typed input/output.

## HTTP client

HTTP client

`turnkey-http` exposes a typed client for all public Turnkey endpoints. The generated sources include:
* Client class (e.g., TurnkeyClient.kt) with suspend functions per endpoint

Where to find things:

```
openapi/
    auth_proxy.swagger.json
    public_api.swagger.json
src/main/kotlin/
    com/turnkey/http/
        TurnkeyClient.kt
```

## Code generation

This package uses a custom generator to produce the Kotlin client from Swagger. The code generation scripts are found in the `tools` module.

### Quick Start

Within this module:
```bash
./gradlew regenerateHttpClient
```

From the repo root:
```bash
./gradlew :packages:http:regenerateHttpClient
```

---

> If you run into this error:

```bash
FAILURE: Build failed with an exception.

* What went wrong:
  25 (or any other number for that matter)
```

> It means the JVM toolchain version you are using is incompatible, please use JVM toolchain 24 or earlier.

---

### What gets generated

`TurnkeyClient.kt` – the HTTP client (OkHttp + coroutines)

## Concurrency & networking

* **OkHttp** under the hood
* **Kotlin coroutines**: requests are suspend and use an internal Call.await() bridge:

```kotlin
private suspend fun Call.await(): Response = suspendCancellableCoroutine { cont ->
    enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            if (!cont.isCompleted) cont.resumeWithException(e)
        }
        override fun onResponse(call: Call, response: Response) {
            if (!cont.isCompleted) cont.resume(response)
        }
    })
    cont.invokeOnCancellation { runCatching { cancel() } }
}
```

* Serialization: kotlinx.serialization.json.Json { ignoreUnknownKeys = true }

## Android notes

* If calling http://localhost on device/emulator, ensure a cleartext policy override for dev:
  * Use networkSecurityConfig with cleartextTrafficPermitted="true" for your debug build, or
  * Use 10.0.2.2 for host loopback on Android emulators
* Keep a single shared `OkHttpClient` per process (connection reuse). If an `OkHttpClient` is not passed in to the `TurnkeyClient` params, a new `OkHttpClient` instance will be created for the client.