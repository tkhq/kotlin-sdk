# Changelog

## 1.0.2 — 2026-11-12
### Patch Changes
- Bouncy Castle (`bcprov-jdk15to18`) is updated from 1.82 to 1.86. Version 1.82 is affected by GHSA-j295-77c3-9frf, GHSA-9pwp-9qqc-pr26 and GHSA-574f-3g2m-x479, which dependency scanners report as critical. `crypto` and `stamper` use Bouncy Castle directly; `http` and `sdk-kotlin` get it through `stamper`.

## 1.0.1 — 2026-06-04
### Patch Changes
- - Export `PRODUCTION_TLS_FETCHER_SIGN_PUBLIC_KEY` constant
- Add `encryptOtpCodeToBundle()` helper that encrypts an OTP code and client public key to the enclave's target key using HPKE

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



