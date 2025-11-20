// buildSrc/src/main/kotlin/Changesets.kt
@file:Suppress("UnstableApiUsage")

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.*
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

abstract class ChangesetsBase : DefaultTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val rootDirProp: DirectoryProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val changesetDir: DirectoryProperty

    @get:Input
    abstract val modules: ListProperty<String>

    protected data class CS(
        val file: File,
        val modules: List<String>,  // Gradle paths like :packages:crypto
        val type: String,           // patch | minor | major
        val summary: String
    )

    /** Default loader: pending files in `.changeset/`. */
    protected fun loadChangesets(): List<CS> =
        loadChangesetsFrom(changesetDir.get().asFile)

    /** Loader from any directory (used by changelog to read `.changeset/applied`). */
    protected fun loadChangesetsFrom(dirFile: File): List<CS> {
        if (!dirFile.exists()) return emptyList()

        val files = dirFile.listFiles { f ->
            f.isFile && (f.name.endsWith(".yml", true)
                    || f.name.endsWith(".yaml", true)
                    || f.name.endsWith(".md", true))
        }?.sortedBy { it.name }.orEmpty()

        return files.flatMap { parseChangesetAny(it) }
    }

    /** Try YAML (array or mapping), then MD front-matter. */
    private fun parseChangesetAny(f: File): List<CS> {
        val text = f.readText()

        // ---- YAML array form ----
        // title: ...
        // type: minor
        // packages: [ :packages:types, :packages:sdk-kotlin ]
        // changelog: |-
        //   lines...
        run {
            val typeScalar = Regex("""(?mi)^\s*type\s*:\s*("?)(major|minor|patch|beta)\1\s*$""")
                .find(text)?.groupValues?.get(2)?.lowercase()

            val arr = Regex("""(?ms)^\s*packages\s*:\s*\[(.*?)\]\s*$""")
                .find(text)?.groupValues?.get(1)
                ?.split(',')
                ?.map { it.trim().trim('"', '\'') }
                ?.filter { it.isNotBlank() }
                .orEmpty()

            if (arr.isNotEmpty()) {
                val summary = extractChangelog(text)
                val t = (typeScalar ?: "patch")
                return listOf(CS(f, arr, t, summary))
            }
        }

        // ---- YAML mapping form (per-module bumps) ----
        // packages:
        //   :packages:types: minor
        //   :packages:sdk-kotlin: patch
        run {
            val mappingBlock = Regex("""(?ms)^\s*packages\s*:\s*\n(.*?)(^\S|\z)""")
                .find(text)?.groupValues?.get(1)
            if (mappingBlock != null) {
                val entries = mappingBlock.lines().mapNotNull { ln ->
                    Regex("""^\s+(.+?)\s*:\s*(major|minor|patch|beta)\s*$""")
                        .find(ln)?.let { m -> m.groupValues[1].trim() to m.groupValues[2].trim() }
                }
                if (entries.isNotEmpty()) {
                    val summary = extractChangelog(text)
                    return entries.map { (mod, bump) -> CS(f, listOf(mod), bump, summary) }
                }
            }
        }

        // ---- Markdown front-matter ----
        // ---
        // type: minor
        // packages:
        //   - :packages:crypto
        //   - :packages:http
        // ---
        // body...
        run {
            val fm = Regex("""(?s)^---\s*(.*?)\s*---\s*(.*)$""").find(text) ?: return@run
            val front = fm.groupValues[1]
            val body  = fm.groupValues[2].trim()

            fun pickScalar(key: String) =
                Regex("""(?mi)^\s*$key\s*:\s*("?)(major|minor|patch|beta)\1\s*$""")
                    .find(front)?.groupValues?.get(2)?.lowercase()

            fun pickList(key: String): List<String> {
                val r = Regex("""(?ms)^\s*$key\s*:\s*\n((?:\s*-\s*.+\n)+)""")
                val mm = r.find(front) ?: return emptyList()
                return mm.groupValues[1].lines().mapNotNull { ln ->
                    Regex("""^\s*-\s*(.+)\s*$""").find(ln)?.groupValues?.get(1)?.trim()
                }.filter { it.isNotBlank() }
            }

            val packs = pickList("packages")
            if (packs.isNotEmpty()) {
                val t = pickScalar("type") ?: "patch"
                return listOf(CS(f, packs, t, body))
            }
        }

        return emptyList()
    }

    private fun extractChangelog(text: String): String {
        val m = Regex("""(?ms)^\s*changelog\s*:\s*\|-\s*\n(.*)$""")
            .find(text)
        val body = m?.groupValues?.get(1) ?: return ""
        return body.lines().joinToString("\n") { it.removePrefix("  ") }.trimEnd()
    }

    /** Bump "MAJOR.MINOR.PATCH[-prerelease]".
     *  - major/minor/patch: bump core and clear prerelease
     *  - beta: add/increment "-beta.X" (does not change core)
     */
    protected fun bumpVersion(ver: String, bump: String): String {
        val m = Regex("""^(\d+)\.(\d+)\.(\d+)(?:-([0-9A-Za-z\.-]+))?$""").matchEntire(ver)
            ?: return ver

        val major = m.groupValues[1].toInt()
        val minor = m.groupValues[2].toInt()
        val patch = m.groupValues[3].toInt()
        val pre   = m.groupValues.getOrNull(4).orEmpty()

        return when (bump.lowercase()) {
            "major" -> "${major + 1}.0.0"
            "minor" -> "$major.${minor + 1}.0"
            "patch" -> "$major.$minor.${patch + 1}"
            "beta"  -> {
                val core = "$major.$minor.$patch"
                if (pre.isEmpty()) {
                    "$core-beta.1"
                } else {
                    val parts = pre.split(".")
                    if (parts.firstOrNull()?.equals("beta", ignoreCase = true) == true) {
                        val last = parts.lastOrNull()
                        val nextNum = if (last != null && last.all { it.isDigit() }) last.toInt() + 1 else 1
                        val base = if (parts.size > 1) parts.dropLast(1).joinToString(".") else "beta"
                        "$core-$base.$nextNum"
                    } else {
                        "$core-beta.1"
                    }
                }
            }
            else    -> ver
        }
    }

    /** Read from build.gradle.kts first, then fallback to gradle.properties. */
    protected fun readModuleVersion(moduleDir: File): String? {
        run {
            val build = File(moduleDir, "build.gradle.kts")
            if (build.exists()) {
                val r = Regex("""(?m)^\s*version\s*=\s*"(.*?)"\s*$""")
                val m = r.find(build.readText())
                if (m != null) return m.groupValues[1]
            }
        }
        run {
            val props = File(moduleDir, "gradle.properties")
            if (props.exists()) {
                val r = Regex("""(?m)^\s*version\s*=\s*(.+?)\s*$""")
                val m = r.find(props.readText())
                if (m != null) return m.groupValues[1].trim().trim('"')
            }
        }
        return null
    }

    /** Write to existing build.gradle.kts version line if present; otherwise gradle.properties. */
    protected fun writeModuleVersion(moduleDir: File, newVer: String): Boolean {
        val build = File(moduleDir, "build.gradle.kts")
        if (build.exists()) {
            val txt = build.readText()
            val re = Regex("""(?m)^(\s*version\s*=\s*")([^"]*)(".*)$""")
            if (re.containsMatchIn(txt)) {
                build.writeText(txt.replace(re) { mr -> "${mr.groupValues[1]}$newVer${mr.groupValues[3]}" })
                return true
            }
        }
        val props = File(moduleDir, "gradle.properties")
        if (props.exists()) {
            val txt = props.readText()
            val re = Regex("""(?m)^\s*version\s*=\s*(.+?)\s*$""")
            val replaced = if (re.containsMatchIn(txt)) {
                txt.replace(re, "version=$newVer")
            } else {
                if (txt.endsWith("\n")) txt + "version=$newVer\n" else txt + "\nversion=$newVer\n"
            }
            props.writeText(replaced)
            return true
        } else {
            props.writeText("version=$newVer\n")
            return true
        }
    }

    /** Gradle path like ':packages:crypto' -> real dir using the provided rootDir. */
    protected fun moduleDirForPath(gradlePath: String): File {
        val relative = gradlePath.removePrefix(":").replace(':', '/')
        return rootDirProp.dir(relative).get().asFile
    }

    protected fun moduleDirForIdentifier(id: String): File {
        // If caller already passed a Gradle path, use it directly.
        if (id.startsWith(":")) return moduleDirForPath(id)

        val artifact = id.substringAfterLast(":") // tolerate "packages:sdk-kotlin" too
        // Try to match against known modules list (full Gradle paths)
        val mods = modules.get()
        val match = mods.firstOrNull { path ->
            // exact last segment match (:packages:sdk-kotlin -> sdk-kotlin)
            path.substringAfterLast(":") == artifact ||
                    // tolerate dashed replacement of colons (packages-sdk-kotlin)
                    path.removePrefix(":").replace(":", "-") == artifact ||
                    // tolerate "packages:sdk-kotlin" style
                    path.removePrefix(":") == id
        } ?: run {
            // Fallback: construct a plausible Gradle path from artifact (sdk-kotlin -> :packages:sdk-kotlin)
            val guess = ":packages:$artifact"
            guess
        }

        val path = match
        return moduleDirForPath(path)
    }
}

abstract class ChangesetsStatusTask : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val changesetDir: DirectoryProperty

    init {
        group = "release"
        description = "Show pending changesets in .changeset/"
        changesetDir.convention(project.layout.projectDirectory.dir(".changeset"))
        // Non-interactive, config-cache safe
    }

    @TaskAction
    fun run() {
        val dir = changesetDir.get().asFile
        if (!dir.exists()) {
            println("No .changeset directory found.")
            return
        }
        val files = dir.listFiles { f ->
            f.isFile && (f.name.endsWith(".yml", true)
                    || f.name.endsWith(".yaml", true)
                    || f.name.endsWith(".md", true))
        }?.sortedBy { it.name }.orEmpty()

        if (files.isEmpty()) {
            println("No pending changesets.")
            return
        }
        println("Pending changesets:")
        files.forEach { println("  - ${it.name}") }
    }
}

abstract class ChangesetsVersionTask : ChangesetsBase() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract override val changesetDir: DirectoryProperty

    @get:Input
    abstract override val modules: ListProperty<String>

    @get:Internal
    protected val bumpedFiles = mutableListOf<File>()

    @TaskAction
    fun run() {
        val items = loadChangesets()
        if (items.isEmpty()) {
            println("No pending changesets to apply.")
            return
        }

        // strongest bump per module (major > minor > patch)
        val rank = mapOf("patch" to 0, "minor" to 1, "major" to 2, "beta" to 3)
        val target = linkedMapOf<String, String>()
        items.forEach { cs ->
            cs.modules.forEach { m ->
                val old = target[m]
                if (old == null || rank.getValue(cs.type) > rank.getValue(old)) {
                    target[m] = cs.type
                }
            }
        }

        println("\nApplying version bumps:")
        target.forEach { (modPath, bump) ->
            val dir = moduleDirForIdentifier(modPath)
            bumpedFiles += File(dir, "gradle.properties")

            val cur = readModuleVersion(dir)
            if (cur == null) {
                println("  - $modPath: NO version line found (skipped)")
                return@forEach
            }
            val next = bumpVersion(cur, bump)
            val ok = writeModuleVersion(dir, next)
            if (ok) println("  - $modPath: $cur → $next ($bump)")
            else    println("  - $modPath: failed to write version")
        }

        // Move applied files to .changeset/applied
        val destDir = changesetDir.dir("applied").get().asFile.also { it.mkdirs() }
        items.forEach { cs ->
            if (!cs.file.exists()) return@forEach
            val to = File(destDir, cs.file.name)
            Files.move(cs.file.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        println("\nMoved ${items.size} file(s) to .changeset/applied/")

        val bumpedListFile = changesetDir.file(".last_bumped_modules").get().asFile
        bumpedListFile.parentFile.mkdirs()
        bumpedListFile.writeText(
            target.keys.joinToString("\n")
        )
        println("Wrote bumped module list to ${bumpedListFile}")
    }
}

abstract class ChangesetsChangelogTask : ChangesetsBase() {

    @TaskAction
    fun run() {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)

        val appliedDir = changesetDir.dir("applied").get().asFile
        if (!appliedDir.exists()) {
            println("No .changeset/applied directory; nothing to write.")
            return
        }

        val applied = loadChangesetsFrom(appliedDir)
        if (applied.isEmpty()) {
            println("No applied changesets found; nothing to write.")
            return
        }

        val repoRoot: File = rootDirProp.get().asFile

        val byModule = applied
            .flatMap { cs -> cs.modules.map { it to (cs.type to cs.summary) } }
            .groupBy({ it.first }, { it.second })

        byModule.forEach { (modPath, entries) ->
            val dir = moduleDirForIdentifier(modPath)
            val newVer = readModuleVersion(dir) ?: "UNSPECIFIED"
            val changelog = File(dir, "CHANGELOG.md")

            val section = buildString {
                appendLine("## $newVer — $today")
                entries.groupBy({ it.first }, { it.second }).forEach { (bump, msgs) ->
                    appendLine("### ${bump.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }} Changes")
                    msgs.forEach { msg -> appendLine("- $msg") }
                }
                appendLine()
            }

            val existing = if (changelog.exists()) changelog.readText() else "# Changelog\n\n"

            // Keep header, insert newest section right after it
            val (header, rest) = if (existing.startsWith("# Changelog")) {
                val split = existing.indexOf("\n\n")
                if (split >= 0) {
                    existing.substring(0, split).trimEnd() to existing.substring(split + 2)
                } else {
                    existing.trimEnd() to ""
                }
            } else {
                "# Changelog" to ("\n\n" + existing)
            }

            val updated = buildString {
                appendLine(header.trimEnd())
                appendLine()
                append(section)
                append(rest)
            }

            changelog.writeText(updated)
            println("Updated ${changelog.relativeTo(repoRoot)}")
        }

        applied.forEach { it.file.delete() }
        if (appliedDir.listFiles().isNullOrEmpty()) {
            appliedDir.delete()
        }
        println("Cleared applied changesets.")
    }
}
