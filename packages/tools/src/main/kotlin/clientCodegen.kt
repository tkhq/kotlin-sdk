import generators.generateClientFile
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.converter.SwaggerConverter
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import utils.*

private fun Schema<*>.refName(): String? = this.`$ref`?.substringAfterLast("/")

/** Entry */
fun main(args: Array<String>) {
    val argv = args.toList()
    fun arg(name: String, default: String? = null): String =
        argv.getOrNull(argv.indexOf(name) + 1) ?: default
        ?: error("Missing $name")

    // ---- Collect repeated --spec <file> [--prefix <Prefix>] pairs
    val specs = mutableListOf<SpecCfg>()
    run {
        var i = 0
        while (i < argv.size) {
            if (argv[i] == "--spec") {
                val p = argv.getOrNull(i + 1) ?: error("Missing value after --spec")
                var prefix = ""
                if (i + 2 < argv.size && argv[i + 2] == "--prefix") {
                    prefix = argv.getOrNull(i + 3) ?: ""
                    i += 2
                }
                specs += SpecCfg(Path.of(p), prefix)
                i += 2
            } else i++
        }
    }
    require(specs.isNotEmpty()) { "At least one --spec is required (use: --spec path [--prefix Prefix])" }

    val outRoot = Path.of(arg("--out"))
    val pkg = arg("--pkg")
    val modelsPkg = arg("--modelsPkg")
    val clientClass = arg("--class", "TurnkeyClient")
    val clientVersionHdr = arg("--clientVersion", "kotlin-sdk/0.1.0")

    specs.forEach { require(Files.exists(it.path)) { "Spec not found: ${it.path}" } }
    outRoot.createDirectories()

    // Parse all specs → OpenAPI 3
    val apis = specs.map { s -> Triple(s, parseToOpenApi3(s.path), readJson(s.path)) }
    generateClientFile(apis, outRoot, pkg, modelsPkg, clientClass, clientVersionHdr)
}

/** Convert Swagger 2.0 to OpenAPI 3 for easier traversal. */
private fun parseToOpenApi3(spec: Path): OpenAPI {
    val opts = ParseOptions().apply { isResolve = true; isFlatten = true }
    return SwaggerConverter().readLocation(spec.toString(), null, opts).openAPI
        ?: error("Failed to parse/convert spec: $spec")
}
