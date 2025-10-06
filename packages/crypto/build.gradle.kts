plugins {
    id("java-library")
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
}

group = "com.turnkey"
version = "1.0-SNAPSHOT"

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(platform(libs.junit.bom))
    testRuntimeOnly(libs.junit.launcher)

    // https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk15on
    implementation(libs.bcprov.jdk15to18)
    implementation(project(":packages:encoding"))

    // Base58Check (decodeChecked/encode helper)
    implementation(libs.bitcoinj.core)

    implementation(libs.kotlinx.serialization.json)
}

kotlin {
    jvmToolchain(24)
}

tasks.test {
    useJUnitPlatform()
}