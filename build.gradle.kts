import com.vanniktech.maven.publish.MavenPublishBaseExtension
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
version = "0.1.0-beta.1"

val wantsCentral: Boolean by lazy {
    val names = gradle.startParameter.taskNames.joinToString(" ")
    names.contains("MavenCentral", ignoreCase = true) ||
            names.contains("Sonatype", ignoreCase = true) ||
            (System.getenv("CI") == "true" && System.getenv("CENTRAL_RELEASE") == "true")
}

configure(publishable.map { project(it) }) {
    apply(plugin = "com.vanniktech.maven.publish")

    extensions.configure<MavenPublishBaseExtension> {

        // Only sign when actually pushing to Central.
        if (wantsCentral) {
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
                developer { id.set("ethan"); name.set("Ethan Konkolowicz") }
            }
        }
    }

    plugins.withId("signing") {
        extensions.configure<SigningExtension> {
            useGpgCmd()
            isRequired = wantsCentral
        }
    }
}

// Convenience tasks
tasks.register("publishSelectedToMavenLocal") {
    dependsOn(publishable.map { "$it:publishToMavenLocal" })
}
tasks.register("publishSelectedToMavenCentral") {
    dependsOn(publishable.map { "$it:publishAllPublicationsToMavenCentral" })
}
