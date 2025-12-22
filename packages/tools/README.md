# com.turnkey.tools

**Internal build tooling** – not published to Maven Central.

Contains code generators that produce:
- **Models.kt** (types package) from OpenAPI specs
- **TurnkeyClient.kt** (http package) from OpenAPI specs

## Structure

```
com/turnkey/tools/
  ├── ClientGenerator.kt   # HTTP client codegen (main entry point)
  ├── TypesGenerator.kt    # Types/models codegen (main entry point)
  └── utils/
      └── Utils.kt         # Shared utilities (parsing, mapping, etc.)
```

## Running codegen

### Generate everything

```bash
./gradlew generate
```

This runs both `regenerateModels` and `regenerateHttpClient` in the correct order.

### Generate individually

**Types only:**
```bash
./gradlew :packages:types:regenerateModels
```

**HTTP client only:**
```bash
./gradlew :packages:http:regenerateHttpClient
```

## Inputs

Both generators read from:
- `openapi/public_api.swagger.json` (Turnkey public API)
- `openapi/auth_proxy.swagger.json` (Turnkey auth proxy)

## Configuration

Generators accept arguments via Gradle task configuration:

**TypesGenerator** (`packages/types/build.gradle.kts`):
- `--out` - Output directory
- `--pkg` - Package name (e.g., `com.turnkey.types`)
- `--types-file-name` - Output filename without `.kt` (default: `Models`)

**ClientGenerator** (`packages/http/build.gradle.kts`):
- `--out` - Output directory
- `--pkg` - Package name (e.g., `com.turnkey.http`)
- `--models-pkg` - Models package for imports (e.g., `com.turnkey.types`)
- `--client-class-name` - Generated class name (default: `TurnkeyClient`)

---

> If you run into this error:

```bash
FAILURE: Build failed with an exception.

* What went wrong:
  25 (or any other number for that matter)
```

> It means the JVM toolchain version you are using is incompatible, please use JVM toolchain 24 or earlier. 