# Changelog

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



