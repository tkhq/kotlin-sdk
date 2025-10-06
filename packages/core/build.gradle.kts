plugins {
    id("com.android.library") version "8.13.0"
    kotlin("android") version "1.9.0"
    kotlin("plugin.serialization") version "2.2.0"
}

group = "com.turnkey"
version = "1.0-SNAPSHOT"

android {
    namespace = "com.turnkey.core"
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
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)

    implementation(project(":packages:encoding"))
    implementation(project(":packages:http"))
    implementation(project(":packages:crypto"))
    implementation(project(":packages:stamper"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.okhttp)
}

kotlin {
    jvmToolchain(24)
}