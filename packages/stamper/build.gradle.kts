plugins {
    id("com.android.library") version "8.13.0"
    kotlin("android") version "1.9.0"
    kotlin("plugin.serialization") version "2.2.20"
}

group = "com.turnkey"
version = "1.0-SNAPSHOT"

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

dependencies {
    implementation(project(":packages:encoding"))
    implementation(project(":packages:passkey"))
    implementation(project(":packages:crypto"))

    // https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk15on
    implementation(libs.bcprov.jdk15to18)

    // JSON (only used for tiny Bundle data classes)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.bcprov.jdk15to18)

}

kotlin {
    jvmToolchain(24)
}