pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = "kotlin-sdk"
include(
    "packages:encoding",
    "packages:crypto",
    "packages:stamper",
    "packages:http",
    "packages:passkey",
    "packages:core",
    ":examples:kotlin-demo-wallet"
)
include(":packages:tools")

project(":packages:core").projectDir = file("packages/core")
project(":packages:stamper").projectDir = file("packages/stamper")
project(":packages:http").projectDir = file("packages/http")
project(":packages:encoding").projectDir = file("packages/encoding")
project(":packages:crypto").projectDir = file("packages/crypto")
project(":examples:kotlin-demo-wallet").projectDir = file("examples/kotlin-demo-wallet")


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://jitpack.io") }
    }
}