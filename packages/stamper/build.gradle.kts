plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization")
    id("com.vanniktech.maven.publish")
}

android {
    namespace = "com.turnkey.stamper"
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

group = "com.turnkey"
version = "1.0.0"

mavenPublishing {
    coordinates("com.turnkey", "stamper", version.toString())
}

dependencies {
    implementation(project(":packages:encoding"))
    implementation(project(":packages:passkey"))
    implementation(project(":packages:crypto"))
    testImplementation(kotlin("test"))
    implementation(libs.bcprov.jdk15to18)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.bcprov.jdk15to18)

}

kotlin {
    jvmToolchain(24)
}