# Changelog

## 1.0.0 — 2026-01-21
### Major Changes
- # v1.0.0 - Initial Stable Release 🎉

### Architecture Improvements

#### Enhanced Error Handling
- **Custom exception hierarchy**: Introduced `TurnkeyCryptoError` sealed class with specific error types:
  - `InvalidCompressedKeyLength` - Key length validation errors
  - `InvalidPrivateLength` / `InvalidPublicLength` - Key size mismatches
  - `InvalidHexString` - Hex decoding failures
  - `OrgIdMismatch` / `UserIdMismatch` - Bundle validation errors
  - `SignatureVerificationFailed` - Enclave signature issues
  - `SignerMismatch` - Enclave public key mismatches
- **Error wrapping**: Unified error handling with `TurnkeyCryptoError.wrap()` for better debugging

#### Removed Dependencies
- **Removed BitcoinJ**: Eliminated `bitcoinj-core` dependency by implementing Base58Check encoding directly in the encoding package

### Breaking Changes

- **Exception types**: `CryptoError` renamed to `TurnkeyCryptoError`
- **Package names**: Internal utilities moved from `com.turnkey.internal` to `com.turnkey.crypto.internal`
- **Namespace changes**: `com.turnkey.utils` → `com.turnkey.crypto.utils`
- **Model imports**: Key types moved to `com.turnkey.crypto.models` package

## 0.1.1 — 2025-11-19
### Patch Changes
- Re-signing artifacts to ensure signature verification works with our uploaded key

## 0.1.0 — 2025-11-17
### Minor Changes
- Initial beta release for Turnkey's Kotlin SDK



