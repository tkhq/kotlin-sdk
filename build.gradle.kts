import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.plugins.signing.SigningExtension

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
    id("com.vanniktech.maven.publish") version "0.34.0" apply false

    kotlin("jvm") version "2.2.20" apply false
    kotlin("plugin.serialization") version "2.2.20" apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
    id("com.android.library") version "8.13.0" apply false
}

val publishable = listOf(
    ":packages:crypto",
    ":packages:encoding",
    ":packages:http",
    ":packages:passkey",
    ":packages:stamper",
    ":packages:types",
    ":packages:sdk-kotlin",
)

group = "com.turnkey"
version = "0.1.0"

val isCi = providers.environmentVariable("CI").map { it == "true" }.orElse(false)
val centralEnv = providers.environmentVariable("CENTRAL_RELEASE").map { it == "true" }.orElse(false)
val taskNames = providers.provider { gradle.startParameter.taskNames.joinToString(" ") }

/** True when we’re publishing to Central (by task name OR CI env flags). */
val centralRequested = providers.provider {
    val names = taskNames.get()
    names.contains("MavenCentral", ignoreCase = true) ||
            names.contains("Sonatype", ignoreCase = true) ||
            (isCi.get() && centralEnv.get())
}

nexusPublishing {
    repositories {
        sonatype {
            username.set(providers.environmentVariable("OSSRH_USERNAME"))
            password.set(providers.environmentVariable("OSSRH_PASSWORD"))
        }
    }
}

configure(publishable.map { project(it) }) {
    apply(plugin = "com.vanniktech.maven.publish")

    // Vanniktech: only wire Central + signing when requested
    extensions.configure<MavenPublishBaseExtension> {
        if (centralRequested.get()) {
            publishToMavenCentral(automaticRelease = true)
            signAllPublications()
        }
        pom {
            name.set(project.name)
            description.set("Turnkey Kotlin SDK module: ${project.name}")
            url.set("https://github.com/tkhq/kotlin-sdk")
            licenses {
                license {
                    name.set("Apache-2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0")
                }
            }
            scm {
                url.set("https://github.com/tkhq/kotlin-sdk")
                connection.set("scm:git:https://github.com/tkhq/kotlin-sdk.git")
                developerConnection.set("scm:git:ssh://git@github.com:tkhq/kotlin-sdk.git")
            }
            developers {
                developer { id.set("turnkey"); name.set("Turnkey") }
            }
        }
    }

    plugins.withId("signing") {
        extensions.configure<SigningExtension> {
            useGpgCmd()
            isRequired = centralRequested.get()
        }
    }
}

val lastBumpedFile = layout.projectDirectory.file(".changeset/.last_bumped_modules")

val bumpedModulesProvider = providers.provider {
    val f = lastBumpedFile.asFile
    if (!f.exists()) emptyList()
    else f.readLines()
        .map { it.trim() }
        .filter { it.isNotEmpty() }
}


tasks.register("publishSelectedToMavenLocal") {
    group = "publishing"
    description = "Publish only modules listed in .changeset/.last_bumped_modules to Maven Local"

    dependsOn(
        bumpedModulesProvider.map { mods ->
            mods.map { mod -> ":packages:$mod:publishToMavenLocal" }
        }
    )

    finalizedBy("printPublishMatrix")
}

// Publish bumped modules to Maven Central
tasks.register("publishSelectedToMavenCentral") {
    group = "publishing"
    description = "Publish only modules listed in .changeset/.last_bumped_modules to Maven Central"

    dependsOn(
        bumpedModulesProvider.map { mods ->
            mods.map { mod -> ":packages:$mod:publishToMavenCentral" }
        }
    )

    finalizedBy("printPublishMatrix")
}

abstract class PrintPublishMatrixTask : DefaultTask() {
    @get:Input abstract val wantsCentral: Property<Boolean>
    @get:Input abstract val lines: ListProperty<String>

    @TaskAction
    fun run() {
        val divider = "─".repeat(92)
        println()
        println("Publishing Matrix  (wantsCentral=${wantsCentral.get()})")
        println(divider)
        println(String.format("%-28s %-36s %-12s %-12s", "Module", "Coordinates (group:artifact:version)", "Type", "Signing"))
        println(divider)
        lines.get().forEach(::println)
        println(divider)
        println("Tip: If an artifactId looks wrong, check your Vanniktech coordinates() or module name.")
        println()
    }
}

val printMatrix = tasks.register<PrintPublishMatrixTask>("printPublishMatrix") {
    wantsCentral.set(centralRequested)
}

/* Build matrix lines after projects are configured */
gradle.projectsEvaluated {
    val matrixLines: List<String> = publishable.flatMap { path ->
        val p = project(path)
        val type = when {
            p.plugins.hasPlugin("com.android.library") -> "android-lib"
            p.plugins.hasPlugin("org.jetbrains.kotlin.jvm") -> "kotlin-jvm"
            p.plugins.hasPlugin("java-library") -> "java-lib"
            else -> "unknown"
        }
        val signing = if (centralRequested.get()) "required" else "not-required"
        val mavenLocale = if (centralRequested.get()) "central" else "local"

        val pubs = p.extensions.findByType(PublishingExtension::class.java)
            ?.publications
            ?.withType(MavenPublication::class.java)
            ?.toList()
            ?: emptyList()

        if (pubs.isNotEmpty()) {
            pubs.mapIndexed { idx, pub ->
                val coords = "${p.group}:${pub.artifactId}:${p.version}"
                val left = if (idx == 0) path.padEnd(28) else " ".repeat(28)
                String.format(
                    "%s %-36s %-12s %-12s  (publication=%s-%s)",
                    left, coords.padEnd(36), type.padEnd(12), signing, pub.name, mavenLocale
                )
            }
        } else {
            val coords = "${p.group}:${p.name}:${p.version}"
            listOf(
                String.format(
                    "%-28s %-36s %-12s %-12s  (no publications found)",
                    path, coords, type, signing
                )
            )
        }
    }

    printMatrix.configure {
        lines.set(matrixLines)
    }

    allprojects {
        tasks.matching { it.name.contains("publish", ignoreCase = true) && it.name.contains("MavenCentral", ignoreCase = true) }
            .configureEach { finalizedBy(":printPublishMatrix") }

        tasks.matching { it.name.contains("publish", ignoreCase = true) && it.name.contains("MavenLocal", ignoreCase = true) }
            .configureEach { finalizedBy(":printPublishMatrix") }
    }
}

// Register tasks
tasks.register<ChangesetsStatusTask>("changesetsStatus") {
    changesetDir.set(layout.projectDirectory.dir(".changeset"))
}
tasks.register<ChangesetsVersionTask>("changesetsVersion") {
    rootDirProp.set(layout.projectDirectory)
    changesetDir.set(layout.projectDirectory.dir(".changeset"))
    modules.set(publishable)
}
tasks.register<ChangesetsChangelogTask>("changesetsChangelog") {
    rootDirProp.set(layout.projectDirectory)
    changesetDir.set(layout.projectDirectory.dir(".changeset"))
    modules.set(publishable)
}
tasks.register<CreateChangesetTask>("createChangeset") {
    modules.set(publishable)
    rootDirProp.set(layout.projectDirectory)
}