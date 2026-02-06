# Changelog

## 1.0.1 — 2026-02-06
### Patch Changes
- Bumped dependencies

## 1.0.0 — 2026-01-21
### Major Changes
- v1.0.0 - Initial Stable Release 🎉

### Architecture Improvements

#### Major Restructuring
- **Removed storage primitives from public API**: `KeyPairStore` and `SecureStore` no longer exported
  - These are now internal implementation details
  - Use `TurnkeyContext` methods instead

#### Enhanced Error Handling
- **Unified error types**: Introduced comprehensive error sealed classes
  - `TurnkeyKotlinError` - Main SDK error hierarchy
  - `TurnkeyStorageError` - Storage-specific errors
- **Error context**: All errors include underlying cause for debugging
- **Better error messages**: Descriptive messages with actionable information

### API Changes

#### New TurnkeyContext Configuration
- **organizationId parameter**: Now required at initialization
  ```kotlin
  TurnkeyContext.init(
      app = this,
      config = TurnkeyConfig(
          apiBaseUrl = "https://api.turnkey.com",
          authProxyBaseUrl = "https://authproxy.turnkey.com",
          authProxyConfigId = "<config-id>",
          organizationId = "<parent-org-id>",  // NEW - Required
          appScheme = "<app-scheme>",
          authConfig = AuthConfig(...)
      )
  )
  ```

#### Breaking Changes
- **Storage primitives removed from public API**: `KeyPairStore` and `SecureStore` no longer exported
  - Migration: Use `TurnkeyContext` methods instead of direct storage access
- **Error types**: Old error classes replaced with `TurnkeyKotlinError` hierarchy
- **File locations**: Import paths changed due to file reorganization
  - `com.turnkey.models` → `com.turnkey.core.models`

## 0.3.0 — 2025-12-11
### Patch Changes
- Made `com.turnkey.passkey` an API level implementation for `sdk-kotlin` that way passkey functionality is accessible through sdk-kotlin.
### Minor Changes
- Added client signature support for OTP authentication support
- Moved `get_accounts` call from `verifyOtp` to `loginOrSignUpWithOtp`
- **BREAKING**: `verifyOtp` changed to no longer check for existing sub-orgs, this functionality was moved to `loginOrSignUpWithOtp`
  - `verifyOtp` no longer takes `contact` or `otpType` as params
  - `verifyOtp` no longer returns a `suborganizationId`

## 0.2.0 — 2025-12-02
### Patch Changes
- Added public key and provider name to the onSuccess callback provided by handle OAuth methods
- Fixed broken OTP flow when "Verification Token Required for Account Lookups" was enabled in the Auth Proxy.
### Minor Changes
- Removed `methods`, `sessionExpirationSeconds`, `otpAlphanumeric`, and `otpLength` from the base config since they are params that are only configurable from the Dashboard and have no affect when changed in the provider config.

## 0.1.1 — 2025-11-19
### Patch Changes
- Re-signing artifacts to ensure signature verification works with our uploaded key

## 0.1.0 — 2025-11-17
### Minor Changes
- Initial beta release for Turnkey's Kotlin SDK



