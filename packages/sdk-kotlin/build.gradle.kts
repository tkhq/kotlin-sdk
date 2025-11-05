plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization")
}

group = "com.turnkey"
version = "0.1.0-beta.1"

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

mavenPublishing {
    coordinates("com.turnkey", "sdk-kotlin", version.toString())
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)

    api(project(":packages:encoding"))
    api(project(":packages:crypto"))
    api(project(":packages:stamper"))
    api(project(":packages:http"))
    api(project(":packages:types"))
    implementation(project(":packages:passkey"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.okhttp)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.appcompat)
}

kotlin {
    jvmToolchain(24)
}