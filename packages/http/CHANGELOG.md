# Changelog

## 2.3.0 — 2026-11-12
### Patch Changes
- Bouncy Castle (`bcprov-jdk15to18`) is updated from 1.82 to 1.86. Version 1.82 is affected by GHSA-j295-77c3-9frf, GHSA-9pwp-9qqc-pr26 and GHSA-574f-3g2m-x479, which dependency scanners report as critical. `crypto` and `stamper` use Bouncy Castle directly; `http` and `sdk-kotlin` get it through `stamper`.
- Activity polling now queries the organization the activity belongs to. Previously `get_activity` polls used the client's configured `organizationId`, so polling an activity submitted to a different organization (for example a sub-organization) could fail.
### Minor Changes
- Submit calls now throw `TurnkeyHttpError.MfaRequired` when an activity has status `ACTIVITY_STATUS_AUTHENTICATORS_NEEDED`. Previously the client kept polling, although polling cannot change this status, and the call then failed with `ActivityNotCompleted` after the retries ran out. The client now stops at once. `MfaRequired` carries the activity and its MFA statuses from `getMfaStatus`, so callers can show which authentication methods are required and then approve the activity with one of them. Code that caught `ActivityNotCompleted` for this status must catch `MfaRequired` instead.
- `TurnkeyClient` now accepts any `RequestStamper`, a new interface in `stamper`, instead of only the `Stamper` class. This lets apps sign requests with keys the SDK does not hold, such as Android Keystore keys in StrongBox or the TEE. `Stamper` implements `RequestStamper`, so existing code compiles without changes. The new `ApiKeyStamp` helper builds the `X-Stamp` header from a public key and a DER signature. `TurnkeyContext.createTurnkeyClient` also accepts a `RequestStamper`. The `TurnkeyClient` constructor signature changes, so code compiled against the previous version must be recompiled.
- Sync generated HTTP client to `v2026.11.1`. Adds 31 endpoints, including `mintJwt`, wallet authenticators, external signers, `getActivePolicies`, fiat on-ramp and off-ramp, and TVC operators and quorum keys. Removes the velocity control endpoints.

## 2.2.0 — 2026-10-06
### Minor Changes
- Submit methods now throw `TurnkeyHttpError.ActivityNotCompleted` instead of a bare `RuntimeException("No result found from ...")` when the activity is not `ACTIVITY_STATUS_COMPLETED` after polling. The exception carries the full `V1Activity` (`id`, `status`, `failure`) so callers can tell `PENDING` from `FAILED`/`REJECTED` and resume by polling `getActivity` instead of re-submitting (#95). Note: `TurnkeyHttpError` extends `Exception`, so code that caught `RuntimeException` around submit calls should catch `TurnkeyHttpError.ActivityNotCompleted` or `Exception`.

## 2.1.1 — 2026-10-01
### Patch Changes
- Fix `solSendTransaction`, `executeSwap` and `ethUndelegate7702` (and their `stamp*` variants) posting an activity `type` that did not match the generated request body, which the API rejected with HTTP 400 (#95). The client generator now takes the activity type from the request schema's declared `type` unless `VersionedActivityTypes` pins a version.

## 2.1.0 — 2026-08-28
### Minor Changes
- Sync generated HTTP client to `v2026.8.3`

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



