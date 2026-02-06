# Changelog

## 1.0.1 — 2026-02-06
### Patch Changes
- Bumped dependencies

## 1.0.0 — 2026-01-21
### Major Changes
- v1.0.0 - Initial Stable Release 🎉

### Architecture Improvements

#### Enhanced Error Handling
- **Unified error types**: Introduced `TurnkeyPasskeyError` sealed class
  - `DecodeFailed` - JSON/Base64 decoding failures
  - `HandleRegistrationResultFailed` - Registration response parsing errors
  - `HandleAssertionResultFailed` - Assertion response parsing errors
  - `OperationFailed` - Generic wrapper for unexpected errors
- **Error wrapping**: All public APIs now wrap exceptions in `TurnkeyPasskeyError`
- **Better error context**: Each error type includes the underlying throwable for debugging

### API Changes

#### PasskeyStamper Enhancement
- **New constructor parameter**: `allowedCredentials` can now be set at initialization
  ```kotlin
  // Old: specify allowed credentials on each assertion
  stamper.assert(challenge, allowedCredentials = listOf(...))

  // New: set allowed credentials once at initialization
  val stamper = PasskeyStamper(
      activity = this,
      rpId = "example.com",
      allowedCredentials = listOf(...)  // NEW
  )
  stamper.assert(challenge)  // Uses instance-level allowed credentials

  // Override per-assertion still supported
  stamper.assert(challenge, allowedCredentials = differentList)
  ```

#### Improved Function Signatures
- **createPasskey()**: Now wraps all exceptions in `TurnkeyPasskeyError`
- **PasskeyStamper.assert()**: Merges instance-level and call-level allowed credentials
- **Internal constructors**: Simplified `PasskeyRequestBuilder` no longer requires `Activity` reference

### Breaking Changes

- **Error types**: Generic exceptions replaced with `TurnkeyPasskeyError` sealed class
- **Constructor changes**: `PasskeyRequestBuilder` no longer requires `Activity` parameter

## 0.1.2 — 2025-12-11
### Patch Changes
- Updated dependencies:
 - `com.turnkey.types@v0.1.2`

## 0.1.1 — 2025-11-19
### Patch Changes
- Re-signing artifacts to ensure signature verification works with our uploaded key

## 0.1.0 — 2025-11-17
### Minor Changes
- Initial beta release for Turnkey's Kotlin SDK



