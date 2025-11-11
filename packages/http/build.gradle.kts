plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization")
    id("com.vanniktech.maven.publish")
}

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

group = "com.turnkey"
version = "0.0.0"

mavenPublishing {
    coordinates("com.turnkey", "http", version.toString())
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.coroutines.core)
    implementation(project(":packages:stamper"))
    implementation(project(":packages:encoding"))
    implementation(project(":packages:types"))
    // tests
    testImplementation(kotlin("test"))
}

val publicSpec: String = file("$projectDir/openapi/public_api.swagger.json").absolutePath
val proxySpec: String = file("$projectDir/openapi/auth_proxy.swagger.json").absolutePath
val out: String = file("$projectDir/src/main/kotlin").absolutePath

val clientCodegen = tasks.register<JavaExec>("client-codegen") {
    group = "client-codegen"
    description = "Generate single-file TurnkeyClient from Swagger"
    mainClass.set("ClientCodegenKt")
    classpath = project(":packages:tools").configurations.named("runtimeClasspath").get() +
            project(":packages:tools").sourceSets["main"].output

    args(
        "--spec", publicSpec,   "--prefix", "",        // public
        "--spec", proxySpec,            "--prefix", "Proxy",   // proxy (also used as model name prefix)
        "--out", out,
        "--pkg", "com.turnkey.http",
        "--modelsPkg", "com.turnkey.types",
        "--class", "TurnkeyClient",
        "--typesFileName", "Models",
        "--clientVersion", "kotlin-sdk/0.1.0",
        "--warning-mode", "all"
    )
}


tasks.register("regenerateHttpClient") {
    group = "clientCodegen"
    description = "Generate client"
    dependsOn(clientCodegen)
}

kotlin {
    jvmToolchain(24)
}