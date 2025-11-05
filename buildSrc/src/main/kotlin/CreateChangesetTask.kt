import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

abstract class CreateChangesetTask : DefaultTask() {

    @get:Input
    abstract val modules: ListProperty<String>

    /** Set this from the root script: layout.projectDirectory */
    @get:Internal
    abstract val rootDirProp: DirectoryProperty

    init {
        group = "release"
        description = "Interactively create a changeset YAML in .changeset/"
        // Interactive => disable config cache for this task
        notCompatibleWithConfigurationCache("Interactive CLI")
    }

    private val reader: () -> String = run {
        val cons = System.console()
        ({
            if (cons != null) cons.readLine() ?: ""
            else {
                val br = BufferedReader(InputStreamReader(System.`in`, StandardCharsets.UTF_8))
                br.readLine() ?: ""
            }
        })
    }

    @TaskAction
    fun run() {
        fun ask(prompt: String, allowEmpty: Boolean = false): String {
            while (true) {
                println(prompt)
                val input = reader().trim()
                if (input.isNotEmpty() || allowEmpty) return input
                println("Please enter a value.")
            }
        }

        val mods = modules.get()
        println("\nSelect modules to include (comma/space separated indexes):")
        mods.forEachIndexed { i, p -> println(String.format("[%d] %s", i + 1, p)) }
        val selRaw = ask("\nYour selection (e.g. 1,3 5):")
        val idxs = selRaw.split(",", " ", "\t")
            .mapNotNull { it.trim().takeIf { s -> s.isNotEmpty() }?.toIntOrNull()?.minus(1) }
            .filter { it in mods.indices }
            .distinct()
        if (idxs.isEmpty()) {
            println("No valid selection. Aborting.")
            return
        }
        val chosen = idxs.map { mods[it] }

        // Bump per module
        val bumps = mutableMapOf<String, String>()
        println("\nBump type per module (default: patch). Accepts: major / minor / patch / beta")
        chosen.forEach { path ->
            val b = ask("- $path bump [major|minor|patch|beta]:", allowEmpty = true)
                .lowercase(Locale.ROOT)
                .let { if (it in setOf("major", "minor", "patch", "beta")) it else "patch" }
            bumps[path] = b
        }

        // Title & note
        val title = ask("\nShort title for this changeset (one line):")
        println("\nWrite changelog note. End with a single '.' on its own line:")
        val noteLines = buildList {
            while (true) {
                val ln = reader()
                if (ln == ".") break
                add(ln)
            }
        }
        val note = noteLines.joinToString("\n").ifBlank { "No additional notes." }

        // Paths from injected property (no Task.project access here)
        val root = rootDirProp.get().asFile.toPath()
        val dir = root.resolve(".changeset")
        Files.createDirectories(dir)

        val idStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
        val slug = title.lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9]+"), "-")
            .trim('-')
            .ifEmpty { "changes" }
        val file = dir.resolve("$idStamp-$slug.yml")

        fun artifactFromPath(p: String): String = p.substringAfterLast(":")

        val yaml = buildString {
            appendLine("title: ${escapeYaml(title)}")
            appendLine("packages:")
            bumps.forEach { (path, bump) ->
                appendLine("  ${escapeYaml(artifactFromPath(path))}: $bump")
            }
            appendLine("changelog: |-")
            note.lines().forEach { appendLine("  $it") }
        }

        Files.writeString(file, yaml)
        println("\n✅ Created ${file.toAbsolutePath()}")
        println("   Preview:\n--------------------------------")
        println(yaml)
        println("--------------------------------")
        println("Tip: commit the file and run `./gradlew changesetsStatus`.")
    }

    private fun escapeYaml(s: String): String {
        return if (s.isEmpty() || s.any { it in ":{}[]#,&*!?|>'\"%@`" }) {
            "\"" + s.replace("\"", "\\\"") + "\""
        } else s
    }
}
