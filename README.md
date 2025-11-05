# Turnkey Kotlin SDK

The Turnkey **Kotlin/Android** SDKs provide everything you need to build a fully working Android app powered by Turnkey: typed HTTP access, auth flows (OAuth / Passkeys / OTP), session + key management, and wallet utilities.

---

## Packages

| Package        | Description                                                                                                                                               | Path                                                      |
|----------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------|
| **sdk-kotlin** | High‑level Android/Kotlin SDK: init/config, session lifecycle (persist/select/auto‑refresh), OAuth/Passkey/OTP helpers, wallet CRUD, and signing helpers. | [`packages/sdk-kotlin/`](./packages/sdk-kotlin/README.md) |
| **http**       | Lower‑level, fully typed HTTP client generated from OpenAPI (OkHttp + coroutines + kotlinx.serialization).                                                | [`packages/http/`](./packages/http/README.md)             |
| **crypto**     | P‑256 key utilities and Turnkey bundle (en/de)cryption helpers                                                                                            | [`packages/crypto/`](./packages/crypto/)                  |
| **encoding**   | Fast hex <> bytes, Base64url helpers, and secure random.                                                                                                  | [`packages/encoding/`](./packages/encoding/README.md)     |
| **passkey**    | Simple passkey registration/assertion wrappers over Credential Manager for Android.                                                                       | [`packages/passkey/`](./packages/passkey/README.md)       |
| **stamper**    | Produces HTTP request stamps from **API keys** or **passkeys** (header pair), consumed by `http` and usable standalone.                                   | [`packages/stamper/`](./packages/stamper/README.md)       |
| **types**      | Single‑file model set (`Models.kt`) for all Turnkey request/response types (public + auth‑proxy), generated from OpenAPI.                                 | [`packages/types/`](./packages/types/README.md)           |
| **tools**      | Internal codegen utilities used by `types` and `http` (generators, helpers).                                                                              | [`packages/tools/`](./packages/tools/README.md)           |

> See each package’s README for usage details and API docs.

---

## Quick start (Android)

**1) Add dependencies**:

```kotlin
// app/build.gradle.kts
dependencies {
  implementation("com.turnkey:sdk-kotlin:<version>")
  // (transitive) http, types, encoding, crypto, stamper, passkey
}
```

**2) Initialize** in your `Application` class:

```kotlin
class App : Application() {
  override fun onCreate() {
    super.onCreate()

    val createSubOrgParams = CreateSubOrgParams(
        customWallet = CustomWallet(
            walletName = "Wallet 1",
            walletAccounts = listOf(
                V1WalletAccountParams(
                    addressFormat = V1AddressFormat.ADDRESS_FORMAT_ETHEREUM,
                    curve = V1Curve.CURVE_SECP256K1,
                    path = "m/44'/60'/0'/0/0",
                    pathFormat = V1PathFormat.PATH_FORMAT_BIP32
                ),
                V1WalletAccountParams(
                    addressFormat = V1AddressFormat.ADDRESS_FORMAT_SOLANA,
                    curve = V1Curve.CURVE_ED25519,
                    path = "m/44'/501'/0'/0'",
                    pathFormat = V1PathFormat.PATH_FORMAT_BIP32
                )
            )
        )
    )
      
    TurnkeyContext.init(
      this,
      TurnkeyConfig(
        apiBaseUrl = "https://api.turnkey.com",
        authProxyBaseUrl = "https://authproxy.turnkey.com",
        authProxyConfigId = "<your-auth-proxy-config-id>",
        appScheme = "<your-app-scheme>", // for OAuth deep link
        authConfig = AuthConfig(
          rpId = "<your-rp-id>",
          createSubOrgParams = MethodCreateSubOrgParams(
              emailOtpAuth = createSubOrgParams,
              smsOtpAuth = createSubOrgParams,
              passkeyAuth = createSubOrgParams,
              oAuth = createSubOrgParams
          ),
        )
      )
    )
  }
}
```

**3) Log in (examples)**

```kotlin
// OTP
val init = TurnkeyContext.initOtp(
    otpType = OtpType.OTP_TYPE_EMAIL,
    contact = "you@example.com"
)

val session = TurnkeyContext.loginOrSignUpWithOtp(
    otpId = init.otpId,
    otpCode = "123ABC",
    contact = "you@example.com",
    otpType = OtpType.OTP_TYPE_EMAIL
)

// Passkey
val passkey = TurnkeyContext.loginWithPasskey(activity = requireActivity())

// OAuth (Google)
TurnkeyContext.handleGoogleOAuth(activity = requireActivity())
```

**4) Use the client**

```kotlin
lifecycleScope.launch {
    val signature = TurnkeyContext.signMessage(
        signWith = "0x...address...",
        addressFormat = V1AddressFormat.ADDRESS_FORMAT_ETHEREUM,
        message = "Hello Turnkey!"
    )
    println("${signature.r}${signature.s}${signature.v}")
}
```

---

## Code generation

This repo uses a **tools** module to generate models and the typed HTTP client from Swagger.

| Artifact  | Task                                            | Inputs                                                                                             | Outputs                                                           |
|-----------|-------------------------------------------------|----------------------------------------------------------------------------------------------------|-------------------------------------------------------------------|
| **types** | `./gradlew :packages:types:regenerateModels`    | `packages/types/openapi/public_api.swagger.json`, `packages/types/openapi/auth_proxy.swagger.json` | `packages/types/src/main/kotlin/com/turnkey/types/Models.kt`      |
| **http**  | `./gradlew :packages:http:regenerateHttpClient` | `packages/http/openapi/public_api.swagger.json`, `packages/http/openapi/auth_proxy.swagger.json`   | `packages/http/src/main/kotlin/com/turnkey/http/TurnkeyClient.kt` |

> Exact task names/flags live in each module’s `build.gradle(.kts)` and are documented in the package READMEs.

---

## Repository layout

```
packages/
  encoding/
  crypto/
  passkey/
  stamper/
  types/
  http/
  sdk-kotlin/
  tools/
examples/
  kotlin-demo-wallet/
```

---

## Development

### Requirements

* **JDK/Toolchain:** Kotlin JVM toolchain 24 (see module `kotlin { jvmToolchain(24) }`)
* **Android:** compileSdk 36 (for Android modules), minSdk varies per package (e.g., passkey/stamper minSdk 28)

### Build & test

```bash
./gradlew build
./gradlew test
```

---

## Releasing

This repo uses the Vanniktech Maven Publish plugin.

```bash
 comming soon
```

> CI usually publishes in dependency order. Only modules with version changes will be released. Use prerelease tags like `0.1.0‑beta.1` when needed.

---

## Links

* Turnkey product docs: [https://docs.turnkey.com](https://docs.turnkey.com)
* Public API reference: [https://docs.turnkey.com/api](https://docs.turnkey.com/api)

---

## Security

* Private keys and mnemonics never leave the client; keep logging redacted.
* For passkeys, configure **Digital Asset Links** so your domain (RP ID) is associated with your app.
* For dev loopback, prefer `10.0.2.2` on Android emulators; allow cleartext only in debug builds.

---

## License

Apache-2.0

---

## Contributing

1. Open an issue or draft PR for discussion.
2. Keep changes small and well‑scoped.
3. Add tests for bugfixes and new features.

---