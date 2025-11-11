plugins {
    id("java-library")
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.vanniktech.maven.publish")
}

group = "com.turnkey"
version = "0.0.0"

mavenPublishing {
    coordinates("com.turnkey", "crypto", version.toString())
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(project(":packages:encoding"))
    testImplementation(platform(libs.junit.bom))
    testRuntimeOnly(libs.junit.launcher)
    implementation(libs.bcprov.jdk15to18)
    implementation(libs.bitcoinj.core)
    implementation(libs.kotlinx.serialization.json)
}

kotlin {
    jvmToolchain(24)
}

tasks.test {
    useJUnitPlatform()
}