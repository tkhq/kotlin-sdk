# Changelog

## 2.0.0 — 2026-06-04
### Major Changes
- ### `INIT_OTP`
`ACTIVITY_TYPE_INIT_OTP_V2` → `ACTIVITY_TYPE_INIT_OTP_V3`
**What changed:** Added required `otpEncryptionTargetBundle` to the result.

### `VERIFY_OTP`
`ACTIVITY_TYPE_VERIFY_OTP` → `ACTIVITY_TYPE_VERIFY_OTP_V2`
**What changed:** Replaced plaintext `otpCode` + `publicKey` with a single `encryptedOtpBundle`.
* Instead of sending the OTP code in plaintext, you now HPKE-encrypt it (along with your public key) to Turnkey's enclave using the `otpEncryptionTargetBundle` returned by `initOtp`. This ensures the OTP code never leaves the client in plaintext.

### `OTP_LOGIN`
`ACTIVITY_TYPE_OTP_LOGIN` → `ACTIVITY_TYPE_OTP_LOGIN_V2`
**What changed:** `clientSignature` promoted from optional to required.

### `CREATE_OAUTH_PROVIDERS`
`ACTIVITY_TYPE_CREATE_OAUTH_PROVIDERS` → `ACTIVITY_TYPE_CREATE_OAUTH_PROVIDERS_V2`
**What changed:** Added `oidcClaims` as a new option alongside `oidcToken`; you must provide exactly one. This updated type feeds into the `CREATE_SUB_ORGANIZATION` and `CREATE_USERS` changes below.

### `CREATE_SUB_ORGANIZATION`
`ACTIVITY_TYPE_CREATE_SUB_ORGANIZATION_V7` → `ACTIVITY_TYPE_CREATE_SUB_ORGANIZATION_V8`
**What changed:** `rootUsers` items updated from `v1RootUserParamsV4` → `v1RootUserParamsV5`, which updates `oauthProviders` from `v1OauthProviderParams` → `v1OauthProviderParamsV2`.

### `CREATE_USERS`
`ACTIVITY_TYPE_CREATE_USERS_V3` → `ACTIVITY_TYPE_CREATE_USERS_V4`
**What changed:** `users` items updated from `v1UserParamsV3` → `v1UserParamsV4`, which updates `oauthProviders` from `v1OauthProviderParams` → `v1OauthProviderParamsV2`.

## 1.0.2 — 2026-02-20
### Patch Changes
- Synced with mono v2026.2.5

## 1.0.1 — 2026-02-06
### Patch Changes
- Synced with `v2026.2.2` of mono

## 1.0.0 — 2026-01-21
### Major Changes
- v1.0.0 - Initial Stable Release 🎉

### Architecture Improvements

#### Enhanced Error Handling
- **Enhanced error types**: New errors with cause tracking
  - `OperationFailed` - New generic wrapper with custom message and cause
- **Better debugging**: All errors include underlying cause for stack traces

### API Changes

#### TurnkeyClient Constructor
- **New organizationId parameter**: Required for fallback org ID across all requests
  ```kotlin
  val client = TurnkeyClient(
      apiBaseUrl = "https://api.turnkey.com",
      stamper = Stamper.fromPublicKey("<public_key_hex>"),
      organizationId = "<your-org-id>",  // NEW - Required
      authProxyUrl = "https://authproxy.turnkey.com",
      authProxyConfigId = null
  )
  ```

#### Breaking Changes
- **Error class rename**: `TurnkeyHttpErrors` → `TurnkeyHttpError`
- **organizationId required**: Must be provided in TurnkeyClient constructor
- **Dependency scope**: `stamper`, `encoding`, `types` are now `api` dependencies (transitive)

## 0.1.2 — 2025-12-11
### Patch Changes
- Synced `com.turnkey.types` and `com.turnkey.http` with v2025.12.2 of `mono`

## 0.1.1 — 2025-11-19
### Patch Changes
- Re-signing artifacts to ensure signature verification works with our uploaded key

## 0.1.0 — 2025-11-17
### Minor Changes
- Initial beta release for Turnkey's Kotlin SDK



