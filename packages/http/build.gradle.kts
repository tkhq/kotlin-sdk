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
version = "0.1.2"

mavenPublishing {
    coordinates("com.turnkey", "http", version.toString())
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.coroutines.core)
    api(project(":packages:stamper"))
    api(project(":packages:encoding"))
    api(project(":packages:types"))
    // tests
    testImplementation(kotlin("test"))
}

val clientCodegen = tasks.register<JavaExec>("client-codegen") {
    group = "client-codegen"
    description = "Generate single-file TurnkeyClient from Swagger"
    mainClass.set("com.turnkey.tools.ClientGeneratorKt")
    classpath = project(":packages:tools").configurations.named("runtimeClasspath").get() +
            project(":packages:tools").sourceSets["main"].output

    args(
        "--out", file("$projectDir/src/main/kotlin").absolutePath,
        "--pkg", "com.turnkey.http",
        "--models-pkg", "com.turnkey.types",
        "--client-class-name", "TurnkeyClient"
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