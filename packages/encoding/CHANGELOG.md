# Changelog

## 1.0.0 — 2026-01-21
### Major Changes
- v1.0.0 - Initial Stable Release 🎉

### Architecture Improvements

#### Enhanced Error Handling
- **Renamed exception hierarchy**: `TurnkeyDecodingException` → `TurnkeyEncodingError`
- **Comprehensive error types**: New sealed class with specific error cases:
  - `OddLengthString` - Hex string length validation (now includes length in error)
  - `InvalidHexCharacter` - Reports character and position for debugging
  - `InvalidBase64Url` - Base64url decoding failures
  - `InvalidBase58Check` - Base58Check checksum verification failures
  - `InvalidUTF8` - UTF-8 decoding errors
  - `OperationFailed` - Generic wrapper for unexpected errors
- **Error wrapping**: Unified error handling with `TurnkeyEncodingError.wrap()` for better exception chaining
- **Consistent error handling**: All functions now use try-catch with proper error wrapping

#### New Dependencies
- **Added BitcoinJ**: Added `bitcoinj-core` dependency for Base58Check encoding operations
  - Used specifically for Base58Check encoding/decoding with checksum verification
  - Enables Solana key format support in the crypto package

### API Changes

#### Breaking Changes
- **Exception renaming**: `TurnkeyDecodingException` → `TurnkeyEncodingError`
- **Enhanced error details**: `OddLengthString` now includes the actual length found
- **Error types**: All decoding functions now throw `TurnkeyEncodingError` instead of generic exceptions

## 0.1.1 — 2025-11-19
### Patch Changes
- Re-signing artifacts to ensure signature verification works with our uploaded key

## 0.1.0 — 2025-11-17
### Minor Changes
- Initial beta release for Turnkey's Kotlin SDK



