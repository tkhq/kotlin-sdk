# com.turnkey.passkey

Simple Android/Kotlin helpers for creating and asserting passkeys via Credential Manager, tailored for Turnkey flows.

* **Register** a new passkey (returns a challenge + `V1Attestation` payload you can send to Turnkey)
* **Assert** (sign) a server-provided challenge using an existing passkey
* Clean APIs for both `Activity` and `Fragment`

> Uses Kotlin coroutines and your app’s Activity as the UI anchor for the Credential Manager sheet.

## Installation

Gradle (Kotlin DSL):
```kotlin
dependencies {
    implementation("com.turnkey:passkey:<version>")
    // Turnkey types (for V1Attestation)
    implementation("com.turnkey:types:<version>")
    // Optional: hex/base64url helpers used in examples below
    implementation("com.turnkey:encoding:<version>")

    // Android Credential Manager (recommended)
    implementation("androidx.credentials:credentials:<latest>")
    implementation("androidx.credentials:credentials-play-services-auth:<latest>")
}
```

> Min Android: API 26+ recommended (Credential Manager works on a wide range via Play Services auth backport).
> In production, associate your app with your web RP using Digital Asset Links so your rpId is trusted.

## Android notes & tips

* For emulators and dev builds, you can use test RPs, but production verification requires proper association.
* Keep long-running calls in a coroutine tied to lifecycle (lifecycleScope / viewLifecycleOwner.lifecycleScope).
* If you need to target a specific credential, pass its ID bytes in allowedCredentials.