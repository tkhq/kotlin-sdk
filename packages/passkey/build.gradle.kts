plugins {
    id("com.android.library") version "8.13.0"
    kotlin("android") version "1.9.0"
    kotlin("plugin.serialization") version "2.2.20"
}

group = "com.turnkey"
version = "1.0-SNAPSHOT"

android {
    namespace = "com.turnkey.passkey"
    compileSdk = 36


    defaultConfig {
        minSdk = 28
        proguardFiles("proguard-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }
}

dependencies {
    implementation(project(":packages:encoding"))
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.kotlinx.coroutines.test)
}

kotlin {
    jvmToolchain(24)
}