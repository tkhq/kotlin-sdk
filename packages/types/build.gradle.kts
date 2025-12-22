plugins {
    id("java-library")
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.serialization.core)
    testImplementation(kotlin("test"))
}

group = "com.turnkey"
version = "0.1.2"

mavenPublishing {
    coordinates("com.turnkey", "types", version.toString())
}

val typesCodegen = tasks.register<JavaExec>("types-codegen") {
    group = "types-codegen"
    description = "Generate single-file Turnkey API Models from Swagger"
    mainClass.set("com.turnkey.tools.TypesGeneratorKt")
    classpath = project(":packages:tools").configurations.named("runtimeClasspath").get() +
            project(":packages:tools").sourceSets["main"].output

    args(
        "--out", file("$projectDir/src/main/kotlin").absolutePath,
        "--pkg", "com.turnkey.types",
        "--types-file-name", "Models"
    )
}

tasks.register("regenerateModels") {
    group = "typesCodegen"
    description = "Generate models"
    dependsOn(typesCodegen)
}

kotlin {
    jvmToolchain(24)
}
