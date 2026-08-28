plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization")
}


android {
    namespace = "com.turnkey.core"
    compileSdk = 36

    defaultConfig {
        minSdk = 28
        proguardFiles("proguard-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

group = "com.turnkey"
version = "2.0.1"

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
    api(project(":packages:passkey"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.okhttp)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.appcompat)
}

kotlin {
    jvmToolchain(11)
}