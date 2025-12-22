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


val publicSpec: String = file("$rootDir/openapi/public_api.swagger.json").absolutePath
val proxySpec: String = file("$rootDir/openapi/auth_proxy.swagger.json").absolutePath
val out: String = file("$projectDir/src/main/kotlin").absolutePath

val typesCodegen = tasks.register<JavaExec>("types-codegen") {
    group = "types-codegen"
    description = "Generate single-file Turnkey API Models from Swagger"
    mainClass.set("TypesCodegenKt")
    classpath = project(":packages:tools").configurations.named("runtimeClasspath").get() +
            project(":packages:tools").sourceSets["main"].output

    args(
        "--spec", publicSpec,   "--prefix", "",        // public
        "--spec", proxySpec,            "--prefix", "Proxy",   // proxy (also used as model name prefix)
        "--out", out,
        "--pkg", "com.turnkey.types",
        "--typesFileName", "Models",
        "--clientVersion", "kotlin-sdk/0.1.0",
        "--warning-mode", "all"
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
