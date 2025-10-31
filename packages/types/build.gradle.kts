plugins {
    id("java-library")
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.20"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.serialization.core)
    // tests
    testImplementation(kotlin("test"))
}

group = "com.turnkey"
version = "1.0-SNAPSHOT"

val publicSpec: String = file("$projectDir/openapi/public_api.swagger.json").absolutePath
val proxySpec: String = file("$projectDir/openapi/auth_proxy.swagger.json").absolutePath
val out: String = file("$projectDir/src/main/kotlin").absolutePath

val typesCodegen = tasks.register<JavaExec>("types-codegen") {
    group = "types-codegen"
    description = "Generate single-file Turnkey API Models from Swagger"
    mainClass.set("TypesCodegenKt")
    classpath = project(":packages:tools").configurations.named("runtimeClasspath").get() +
            project(":packages:tools").sourceSets["main"].output

    args(
        "--spec", publicSpec,   "--prefix", "",        // public
        "--spec", proxySpec,            "--prefix", "Proxy",   // proxy (also used as model name prefix)
        "--out", out,
        "--pkg", "com.turnkey.types",
        "--typesFileName", "Models",
        "--clientVersion", "kotlin-sdk/0.1.0",
        "--warning-mode", "all"
    )
}

tasks.register("regenerateModels") {
    group = "typesCodegen"
    description = "Generate models"
    dependsOn(typesCodegen)
}

kotlin {
    jvmToolchain(24)
}
