plugins {
    id("com.android.library") version "8.13.0"
    kotlin("android") version "1.9.0"
    kotlin("plugin.serialization") version "2.2.20"
}

group = "com.turnkey"
version = "1.0-SNAPSHOT"

android {
    namespace = "com.turnkey.http"
    compileSdk = 36

    defaultConfig {
        minSdk = 28
        proguardFiles("proguard-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }

    sourceSets["main"].java.srcDir("${layout.buildDirectory}/src/main/kotlin")
    sourceSets["main"].resources.srcDir("${layout.buildDirectory}/src/main/resources")
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.coroutines.core)
    implementation(project(":packages:stamper"))
    implementation(project(":packages:encoding"))
    // tests
    testImplementation(kotlin("test"))
}

val publicSpec: String = file("$projectDir/openapi/public_api.swagger.json").absolutePath
val proxySpec: String = file("$projectDir/openapi/auth_proxy.swagger.json").absolutePath
val out: String = file("$projectDir/src/main/kotlin").absolutePath

val codegen = tasks.register<JavaExec>("codegen") {
    group = "codegen"
    description = "Generate single-file TurnkeyClient from Swagger"
    mainClass.set("CodegenKt")
    classpath = project(":packages:tools").configurations.named("runtimeClasspath").get() +
            project(":packages:tools").sourceSets["main"].output

    args(
        "--spec", publicSpec,   "--prefix", "",        // public
        "--spec", proxySpec,            "--prefix", "Proxy",   // proxy (also used as model name prefix)
        "--out", out,
        "--pkg", "com.turnkey.http",
        "--typesPkg", "com.turnkey.http",
        "--modelPkg", "com.turnkey.http",
        "--class", "TurnkeyClient",
        "--clientVersion", "kotlin-sdk/0.1.0",
        "--warning-mode", "all"
    )
}

tasks.register("regenerateHttpClient") {
    group = "codegen"
    description = "Generate models + client"
    dependsOn(codegen)
}

kotlin {
    jvmToolchain(24)
}