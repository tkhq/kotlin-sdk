plugins {
    id("java-library")
    kotlin("jvm")
    id("com.vanniktech.maven.publish")
}

group = "com.turnkey"
version = "0.1.0-beta.1"

mavenPublishing {
    coordinates("com.turnkey", "encoding", version.toString())
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(24)
}