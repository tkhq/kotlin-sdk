# Changelog

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



