# com.turnkey.sdk-kotlin

High-level Kotlin/Android SDK for building Turnkey-powered apps. It wraps the lower‑level `turnkey-http` client and adds:

* **Initialization & config** (`TurnkeyContext.init(...)`)
* **Session lifecycle** (persist, select, auto‑refresh, expiry timers)
* **Auth flows**
  * OAuth (Google, Apple, X/Twitter, Discord)
  * Passkeys (Credential Manager)
  * OTP (email / SMS)
* **Key management** (secure storage of generated P‑256 keypairs)
* **Wallet ops** (create, import, export, list)
* **Signing** (typed helpers incl. Ethereum prefixing)

> Targets Android + Kotlin/JVM. Uses coroutines, OkHttp, and kotlinx.serialization.

## Installation
```kotlin
dependencies {
    implementation("com.turnkey:sdk-kotlin:<version>")
    
    
    // Turnkey modules this SDK builds on:
    implementation("com.turnkey:http:<version>") // typed HTTP client (OkHttp)
    implementation("com.turnkey:crypto:<version>") // P-256, (en|de)cryption helpers
    implementation("com.turnkey:encoding:<version>") // hex & base64url utils
    implementation("com.turnkey:passkey:<version>") // passkey registration/assertion
    implementation("com.turnkey:stamper:<version>") // API signing / passkey signing
    implementation("com.turnkey:types:<version>") // generated DTOs
    
    
    // 3rd‑party
    implementation("com.squareup.okhttp3:okhttp:<okhttp>")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:<kotlinx>")
    implementation("androidx.credentials:credentials:<latest>")
    implementation("androidx.credentials:credentials-play-services-auth:<latest>")
}
```

## Quick start

### 1) Configure & initialize

Create a `TurnkeyConfig` and initialize the singleton `TurnkeyContext` from your `Application`.

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // optional params to pass in on sub-org creation
        // in this case, we are creating our sub-orgs with a wallet named "Wallet 1" and two wallet accounts (Ethereum & Solana)
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
            app = this,
            config = TurnkeyConfig(
                apiBaseUrl = "https://api.turnkey.com",
                authProxyBaseUrl = "https://authproxy.turnkey.com",
                authProxyConfigId = "<your-auth-proxy-config-id>",
                organizationId = "<your-parent-organization-id>",
                appScheme = "<your-app-scheme>",
                authConfig = AuthConfig(
                    rpId = "<your-rp-id>",
                    createSubOrgParams = MethodCreateSubOrgParams(
                        emailOtpAuth = createSubOrgParams,
                        smsOtpAuth = createSubOrgParams,
                        passkeyAuth = createSubOrgParams,
                        oAuth = createSubOrgParams
                    )
                )
            )
        )
    }
}
```

### 2) Wait for readiness (optional)

```kotlin
lifecycleScope.launch {
    TurnkeyContext.awaitReady()
    // client available via TurnkeyContext.client
}
```

## Sessions & keys

This SDK manages an authenticated Session (JWT) and the associated API keypair material in secure storage.
* **Key generation**: `createKeyPair()` creates a new P‑256 pair, stores private key, and returns the compressed public key hex.
* **Persist/Select**: `createSession(jwt, sessionKey)` persists a session; `setSelectedSession(sessionKey)` selects it and rebuilds the client.
* **Auto‑refresh**: expiry timers are scheduled; if an auto‑refresh duration was configured, `refreshSession(...)` runs before expiry.
* **Clear**: `clearSession(sessionKey)` deletes a session’s artifacts; `clearAllSessions()` purges everything.

## Auth flows

All flows eventually produce a **session JWT**, persisted via `createSession(...)`.

### OAuth (Google / Apple / X / Discord)

Each helper opens a Custom Tab to the provider, waits for your deep‑link, then logs in or signs up.

> [!NOTE]
> Prereqs: Configure appScheme in TurnkeyConfig, set up your redirect URI and provider client IDs in the Wallet Kit tab on the [dashboard ](https://app.turnkey.com/dashboard/walletKit) (or supply them directly).

> [!IMPORTANT]
> You must add the OAuth redirect activity to your `AndroidManifest.xml`!

```xml

<activity android:name="com.turnkey.core.OAuthRedirectActivity" android:launchMode="singleTop"
    android:noHistory="true" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="<your-app-scheme>" />
    </intent-filter>
</activity>
```
> [!NOTE]
> This activity is required to catch all OAuth redirect requests to complete the OAuth process

```kotlin
binding.acGoogleOAuthButton.setOnClickListener {
    viewLifecycleOwner.lifecycleScope.launch {
        try {
            // more optional params can be passed here (ex:  clientId, originUri, redirectUri, etc...)
            TurnkeyContext.handleGoogleOAuth(
                activity = requireActivity(),
            )
        } catch (t: Throwable) {
            Log.e("AuthStepFragment", "Failed to handle Google OAuth", t)
        }
    }
}
```

You can also intercept the oidcToken by passing onSuccess = { idToken -> … } and performing loginOrSignUpWithOAuth(...) manually.

### Passkeys

* Login: uses `PasskeyStamper` to assert a server challenge via Credential Manager and then `stampLogin`.
* Sign up: creates a platform passkey (registration) and uses it during signup; then logs in.

> Important note: In order to enable passkeys, you must set up a valid rpId. Please follow the Google Documentation for steps on how to do so: https://developer.android.com/training/app-links/verify-applinks.

```kotlin
lifecycleScope.launch {
   try {
       TurnkeyContext.loginWithPasskey(
           activity = requireActivity()
       )
   } catch (t: Throwable) {
       Log.e("AuthStepFragment", "Failed to login with passkey", t)
   }
}
 

lifecycleScope.launch {
   try {
       TurnkeyContext.signUpWithPasskey(
           activity = requireActivity()
       )
   } catch (t: Throwable) {
       Log.e("AuthStepFragment", "Failed to sign up with passkey", t)
   }
}
```
> Ensure your rpId is set in config (authConfig.rpId) or pass it explicitly.

### OTP (email/SMS)

This is a 2 step process:

1) Send the OTP code to an email/phone number

```kotlin
val ( otpId ) = TurnkeyContext.initOtp(
    otpType = OtpType.OTP_TYPE_EMAIL, // or OtpType.OTP_TYPE_SMS
    contact = "example@email.com"
)
```

2) Verify & login / sign up
```kotlin
TurnkeyContext.loginOrSignUpWithOtp(
    otpId = "fdS3...3aSD",
    otpCode = "123ABC",
    contact = "example@email.com",
    otpType = OtpType.OTP_TYPE_EMAIL // or OtpType.OTP_TYPE_SMS
)
```

### Wallets & accounts

Wallets are automatically saved in state variables accessible through the `TurnkeyContext`

```kotlin
val wallets = TurnkeyContext.wallets.value.orEmpty()
```

Wallet API shape:
```kotlin
data class Wallet (
    val id: String,
    val name: String,
    val accounts: List<V1WalletAccount>
)
```

Creating wallets:
```kotlin
// wallet accounts of your choosing (in this case we create Solana & Ethereum accounts)
val accounts = listOf(
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

TurnkeyContext.createWallet(
    walletName = "Wallet-${System.currentTimeMillis()}",
    accounts = accounts,
    mnemonicLength = 12
)
```

Importing wallets:
```kotlin
// user inputs
private val mnemonic = MutableStateFlow("")
private val name = MutableStateFlow("")

try {
    val m = mnemonic.value
    if (m.isEmpty()) return@launch
    val walletName = name.value.ifBlank { "Wallet-${System.currentTimeMillis()}" }

    val accounts = listOf(
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

    TurnkeyContext.importWallet(
        walletName = walletName,
        mnemonic = m,
        accounts = accounts
    )
} catch (t: Throwable) {
    Log.e("Import", "Import Failed", t)
}
```

Export wallet:
```kotlin
try {
    val res = TurnkeyContext.exportWallet(
        walletId = selectedWallet.id
    )
} catch (t: Throwable) {
    Log.e("Dashboard", "Export Failed", t)
}
```

### Signing helpers

Sign raw payload:
```kotlin
lifecycleScope.launch {
    val signed = TurnkeyContext.signRawPayload(
        signWith = "0x…accountOrAddress…",
        payload = "0x48656c6c6f", // or base64/utf8 via encoding below
        encoding = V1PayloadEncoding.PAYLOAD_ENCODING_HEXADECIMAL,
        hashFunction = V1HashFunction.HASH_FUNCTION_KECCAK256,
    )
}
```

Sign human message (EVM prefixing optional)
```kotlin
lifecycleScope.launch {
    val signature = TurnkeyContext.signMessage(
        signWith = "0x...address...",
        addressFormat = V1AddressFormat.ADDRESS_FORMAT_ETHEREUM, // or whichever address format suits your signing account
        message = "Hello Turnkey!"
    )
}
```

### State & flows

Key state is exposed as `StateFlows`:

* `TurnkeyContext.authState`: `StateFlow<AuthState>` — loading / authenticated / unauthenticated
* `TurnkeyContext.session`: `StateFlow<Session?>`
* `TurnkeyContext.user`: `StateFlow<V1User?>`
* `TurnkeyContext.wallets`: `StateFlow<List<Wallet>?>`

Subscribe in UI layers (Compose / Views) to react to auth transitions.

```kotlin
lifecycleScope.launch {
    TurnkeyContext.authState.collect { state -> ... }
}
```

## Android integration notes

* Digital Asset Links: if you rely on passkeys, associate your domain (RP ID) with the app so the OS will permit assertions for that domain.
* Emulator vs device: for local endpoints, use 10.0.2.2 and a debug network security config if you need cleartext.
* Coroutines: all SDK calls are suspend; use lifecycleScope or your DI scope.
* **Android SDK path for CLI builds:** If you are building this SDK or any app that depends on it outside of Android Studio (e.g., using the command line or CI), you must ensure that the Android SDK path is available to the build system. This SDK has Android dependencies and requires the Android SDK to be set up properly.
  * Set the Android SDK path in your environment (e.g., in your `~/.zshrc` or `~/.bashrc`):
    ```sh
    export ANDROID_HOME=~/Library/Android/sdk
    ```
  * If you are building inside Android Studio, this is handled automatically and you do not need to set these variables manually.

---

## Advanced

### Multiple sessions

You can store multiple sessions (e.g., different users) and switch between them:

```kotlin
// Persist under a custom key
TurnkeyContext.createSession(jwt, sessionKey = "work")
TurnkeyContext.createSession(otherJwt, sessionKey = "personal")

// Switch
TurnkeyContext.setSelectedSession("personal")
```

## FAQ

**Why does the SDK generate new keypairs during login?**
The public key is used to stamp login with the Turnkey API and the private key is stored locally to sign subsequent API calls for that session.

**Where are keys stored?**
Through KeyPairStore (backed by your app’s secure storage mechanism). You can purge unused keys via deleteUnusedKeyPairs().

**Do I need the Auth Proxy?**
No, it’s optional. If configured (authProxyConfigId), it can override auth method flags, client IDs, and redirect URIs dynamically from your Turnkey dashboard.