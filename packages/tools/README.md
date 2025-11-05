# com.turnkey.tools

## _**For internal use only**_

Holds the codegen scripts for both client generation & types generation.

To run these codegen scripts, run the following tasks in the repo root (`http` or `types`)

### Client Codegen

```bash
./gradlew :packages:http:regenerateHttpClient
```

### Types Codegen

```bash
./gradlew :projects:types:regenerateModels
```

---

> If you run into this error:

```bash
FAILURE: Build failed with an exception.

* What went wrong:
  25 (or any other number for that matter)
```

> It means the Java version you are using is incompatible, please use Java v24 or earlier. 