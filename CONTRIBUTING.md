# Contributing

## Repo overview

- [`packages`](/packages): Turnkey Kotlin packages.
- [`examples`](/examples): Kotlin usage examples.

## Getting Started

Clone the repo:

```bash
$ git clone https://github.com/tkhq/kotlin-sdk/
$ cd kotlin-sdk
```

- **JDK/Toolchain:** Kotlin JVM toolchain 24
- **Android:** compileSdk 36 (for Android modules), minSdk varies per package (e.g., passkey/stamper minSdk 28)

## Before you open PRs

Before you open a PR, ensure you've added a changeset detailing which packages we're affected, what kind of bump they require, and an appropriate changelog note & title.

### Adding a changeset

1) Go to the repo's root `build.gradle.kts` ([here](./build.gradle.kts)) and run the `createChangeset` task.
   ![create-changeset.png](assets/create-changeset.png)
2) Choose which packages to write changesets for
   ![select-packages.png](assets/select-packages.png)
3) Select the bump type per package (major, minor, patch, or beta)
   ![select-bump-type.png](assets/select-bump-type.png)
4) Add a title and a note (end the note with a "." _on its own line_)
   ![changeset-title-note.png](assets/changeset-title-note.png)

And that's it! Commit your changeset and the CI release tooling will cover the rest (changelogs + versioning)! If done properly, you should see your new changeset in the [.changeset](./.changeset) directory.

> [!NOTE]
> **Important if publishing beta bumps:** `beta` takes precedence over ALL, meaning if a package has a `major` changeset + a `beta` changeset, the final version will look like `0.1.0-beta.1` -> `0.1.0-beta.2`