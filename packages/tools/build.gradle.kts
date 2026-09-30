plugins {
    kotlin("jvm") version "2.2.20"
    application
}

dependencies {
    implementation(libs.swagger.parser)                  // reads Swagger 2.0 & OAS3
    implementation(libs.squareup.kotlinpoet)             // generates Kotlin source
    implementation(libs.kotlinx.serialization.json)
    // tests
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

application {
    // fully-qualified name of main()
    mainClass.set("CodegenKt")
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs = listOf("-Dfile.encoding=UTF-8")
}