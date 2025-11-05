# com.turnkey.stamper

Lightweight signing helper for the Turnkey ecosystem. Produces request stamps from either:

* API keys (P‑256 keypair you control)
* Passkeys (WebAuthn via Android Credential Manager)

It returns a (headerName, headerValue) pair you can attach to HTTP requests – the lower‑level turnkey-http client already knows how to consume this, but you can also wire it to any OkHttp client.

## Installation

Gradle (Kotlin DSL):
```kotlin
dependencies {
    implementation("com.turnkey:stamper:<version>")
    // If you use passkey mode:
    implementation("com.turnkey:passkey:<version>")
    // Utilities used in examples
    implementation("com.turnkey:encoding:<version>")
}
```

> Android minSdk 28 (per module config). JVM toolchain 24.

## API
```kotlin
class Stamper private constructor(
private val apiPublicKey: String?,
private val apiPrivateKey: String?,
private val passkeyManager: PasskeyStamper?
) {
    /** Empty/default constructor – not configured for any mode. */
    constructor()
    
    
    /** API‑key mode. */
    constructor(apiPublicKey: String, apiPrivateKey: String)
    
    
    /** Passkey mode. */
    constructor(passkeyManager: PasskeyStamper)
    
    
    /**
    * Signs the provided payload and returns the header pair to attach.
    * @throws StampError if not configured for any mode
    */
    suspend fun stamp(payload: String): Pair<String, String>
}
```

* API‑key mode -> returns header: `X-Stamp`
* Passkey mode -> returns header: `“X-Stamp-Webauthn”`

`payload` is an opaque canonical string your client/server agree on (see examples below). Internally the payload is hashed with SHA‑256 before signing.

## With `turnkey-http`

You typically don’t call `stamp(...)` yourself, `turnkey-http` will do it per request.

```kotlin
val client = TurnkeyClient(
    apiBaseUrl = "https://api.turnkey.com",
    stamper = Stamper(apiPublicKey = pubKeyHex, apiPrivateKey = privKeyHex)
)

// Or with passkeys
val client2 = TurnkeyClient(
    apiBaseUrl = "https://api.turnkey.com",
    stamper = Stamper(PasskeyStamper(activity = requireActivity(), rpId = "example.com"))
)
```

## Security notes

* Treat `apiPrivateKey` like any other secret. Do not log.
* In passkey mode, ensure your app <> domain association (Digital Asset Links) so assertions are usable for your RP ID.