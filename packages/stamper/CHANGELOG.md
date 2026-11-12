# Changelog

## 1.1.0 — 2026-11-12
### Patch Changes
- Bouncy Castle (`bcprov-jdk15to18`) is updated from 1.82 to 1.86. Version 1.82 is affected by GHSA-j295-77c3-9frf, GHSA-9pwp-9qqc-pr26 and GHSA-574f-3g2m-x479, which dependency scanners report as critical. `crypto` and `stamper` use Bouncy Castle directly; `http` and `sdk-kotlin` get it through `stamper`.
### Minor Changes
- `TurnkeyClient` now accepts any `RequestStamper`, a new interface in `stamper`, instead of only the `Stamper` class. This lets apps sign requests with keys the SDK does not hold, such as Android Keystore keys in StrongBox or the TEE. `Stamper` implements `RequestStamper`, so existing code compiles without changes. The new `ApiKeyStamp` helper builds the `X-Stamp` header from a public key and a DER signature. `TurnkeyContext.createTurnkeyClient` also accepts a `RequestStamper`. The `TurnkeyClient` constructor signature changes, so code compiled against the previous version must be recompiled.

## 1.0.3 — 2026-06-04
### Patch Changes
- Dependency bump

## 1.0.2 — 2026-02-20
### Patch Changes
- Dependency bump

## 1.0.1 — 2026-02-06
### Patch Changes
- Bumped dependencies

## 1.0.0 — 2026-01-21
### Major Changes
- v1.0.0 - Initial Stable Release 🎉

### Architecture Improvements

#### Enhanced Error Handling
- **Unified error hierarchy**: Consolidated multiple error types into `TurnkeyStamperError` sealed class
- **Error wrapping**: All public APIs wrap exceptions in `TurnkeyStamperError`
- **Better error context**: Enhanced error messages with found/expected values

### API Changes

#### Breaking Changes
- **Error types consolidated**:
  - `ApiKeyStampError` → `TurnkeyStamperError`
  - `PasskeyStampError` → `TurnkeyStamperError`
  - `StampError` → `TurnkeyStamperError`
- **Import paths**: `SignatureFormat` moved from `com.turnkey.stamper` to `com.turnkey.stamper.utils`
- **Error handling**: All functions now throw `TurnkeyStamperError` instead of specific error types

#### New Functionality
- **`sign()` method**: New public method for signing arbitrary payloads
  ```kotlin
  val stamper = Stamper.fromPublicKey(pubKey)

  // Sign with DER format (default)
  val derSignature = stamper.sign(payload)

  // Sign with raw r||s format
  val rawSignature = stamper.sign(payload, SignatureFormat.raw)
  ```

#### Improved Error Messages
- **InvalidDigestLength**: Now includes found size
- **InvalidPrivateKeyBytes**: Shows found vs expected size
- **InvalidChallenge**: Shows found vs expected size
- **MismatchedPublicKey**: Shows both expected and found keys

#### Integration
Works seamlessly with `turnkey-http` client:
```kotlin
val client = TurnkeyClient(
    apiBaseUrl = "https://api.turnkey.com",
    stamper = Stamper.fromPublicKey(pubKey)
)
```

## 0.1.2 — 2025-12-11
### Patch Changes
- Added a `sign` function to the API Key Stamper for signing arbitrary payloads.

## 0.1.1 — 2025-11-19
### Patch Changes
- Re-signing artifacts to ensure signature verification works with our uploaded key

## 0.1.0 — 2025-11-17
### Minor Changes
- Initial beta release for Turnkey's Kotlin SDK



