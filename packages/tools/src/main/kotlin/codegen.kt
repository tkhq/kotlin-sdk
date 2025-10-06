import com.squareup.kotlinpoet.*
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.converter.SwaggerConverter
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import utils.*   // expects MAP, STRING, kotlinTypeFromSchema, etc.

/** One spec input + an optional prefix applied to operation/type names. */
data class SpecCfg(val path: Path, val prefix: String)

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

    val outRoot      = Path.of(arg("--out"))
    val pkg          = arg("--pkg")
    val modelPkg     = arg("--modelPkg")
    val clientClass  = arg("--class", "TurnkeyClient")
    val typesPkg     = arg("--typesPkg", pkg)
    val clientVersionHdr = arg("--clientVersion", "kotlin-sdk/0.1.0")

    specs.forEach { require(Files.exists(it.path)) { "Spec not found: ${it.path}" } }
    outRoot.createDirectories()

    // Parse all specs → OpenAPI 3
    val apis: List<Pair<SpecCfg, OpenAPI>> = specs.map { it to parseToOpenApi3(it.path) }

    generateEmptyModelsIfMissing(apis, outRoot, modelPkg)
    generateTypesFile(apis, outRoot, typesPkg, modelPkg)
    generateClientFile(apis, outRoot, pkg, typesPkg, clientClass, clientVersionHdr)
}

/** Convert Swagger 2.0 to OpenAPI 3 for easier traversal. */
private fun parseToOpenApi3(spec: Path): OpenAPI {
    val opts = ParseOptions().apply { isResolve = true; isFlatten = true }
    return SwaggerConverter().readLocation(spec.toString(), null, opts).openAPI
        ?: error("Failed to parse/convert spec: $spec")
}

/* ==========================
 *  TurnkeyClient generation
 * ========================== */
private fun generateClientFile(
    apis: List<Pair<SpecCfg, OpenAPI>>,
    outRoot: Path,
    pkg: String,
    typesPkg: String,
    className: String,
    clientVersionHdr: String,
) {
    val stamperClass = ClassName("com.turnkey.stamper", "Stamper")
    val errorClass = ClassName("com.turnkey.http.utils", "TurnkeyAuthProxyErrors")
    val okHttpClient = ClassName("okhttp3", "OkHttpClient")
    val requestCls   = ClassName("okhttp3", "Request")
    val toMediaType  = MemberName("okhttp3.MediaType.Companion", "toMediaType")
    val toReqBody    = MemberName("okhttp3.RequestBody.Companion", "toRequestBody")
    val jsonCls      = ClassName("kotlinx.serialization.json", "Json")

    val stringT = String::class.asTypeName()
    val nullableStringT = stringT.copy(nullable = true)

    val ctor = FunSpec.constructorBuilder()
        .addParameter(
            ParameterSpec.builder("apiBaseUrl", nullableStringT)
                .defaultValue("null")
                .build()
        )
        .addParameter("stamper", stamperClass)
        .addParameter("http", okHttpClient)
        .addParameter(
            ParameterSpec.builder("authProxyUrl", nullableStringT)
                .defaultValue("null")
                .build()
        )
        .addParameter("authProxyConfigId", nullableStringT)
        .build()

    val typeBuilder = TypeSpec.classBuilder(className)
        .addKdoc("HTTP Client for interacting with Turnkey API (generated). DO NOT EDIT BY HAND.\n")
        .primaryConstructor(ctor)
        .addProperty(
            PropertySpec.builder("apiBaseUrl", String::class, KModifier.PRIVATE)
                .initializer("%N ?: %S", "apiBaseUrl", "https://api.turnkey.com")
                .build()
        )
        .addProperty(
            PropertySpec.builder("stamper", stamperClass, KModifier.PRIVATE)
                .initializer("%N", "stamper")
                .build()
        )
        .addProperty(
            PropertySpec.builder("http", okHttpClient, KModifier.PRIVATE)
                .initializer("%N", "http")
                .build()
        )
        .addProperty(
            PropertySpec.builder("authProxyUrl", String::class, KModifier.PRIVATE)
                .initializer("%N ?: %S", "authProxyUrl", "https://authproxy.turnkey.com")
                .build()
        )
        .addProperty(
            PropertySpec.builder("authProxyConfigId", nullableStringT, KModifier.PRIVATE)
                .initializer("%N", "authProxyConfigId")
                .build()
        )
        .addProperty(
            PropertySpec.builder("json", jsonCls, KModifier.PRIVATE)
                .initializer("%T { ignoreUnknownKeys = true }", jsonCls)
                .build()
        )

    // For each spec and each operation, generate a method.
    apis.forEach { (cfg, openAPI) ->
        val opPrefix = cfg.prefix
        val baseVarName = if (opPrefix.isBlank()) "apiBaseUrl" else "authProxyUrl"
        val isProxy = !opPrefix.isBlank()

        openAPI.paths.orEmpty().forEach { (path, item) ->
            listOfNotNull("get" to item.get, "post" to item.post).forEach { (method, op) ->
                if (op == null) return@forEach
                val rawId = op.operationId?.takeIf { it.isNotBlank() } ?: return@forEach
                val opId = rawId.substringAfter("_")
                val methodName = if (isProxy) opPrefix.lowercase() + opId else opId.replaceFirstChar { it.lowercaseChar() }

                // Resolve request & response schema refs
                val reqSchemaRef = if (method == "post")
                    op.requestBody?.content?.get("application/json")?.schema?.`$ref` else null
                val respSchemaRef = op.responses["200"]
                    ?.content?.get("application/json")
                    ?.schema?.`$ref`

                // Body DTO type (if present)
                val bodyDto: ClassName? = reqSchemaRef?.let { ref ->
                    val schemaName = opId.substringAfter("_")
                    val typeName = (opPrefix.ifBlank { "" }) + "T" + schemaName + "Body"
                    ClassName(typesPkg, typeName)
                }

                // Response type (or Unit)
                val respType: TypeName = respSchemaRef?.let { ref ->
                    val schemaName = opId.substringAfter("_")
                    val typeName = (opPrefix.ifBlank { "" }) + "T" + schemaName + "Response"
                    ClassName(typesPkg, typeName)
                } ?: UNIT

                val isDeprecated = op.deprecated == true
                val kdoc = buildString {
                    append("${method.uppercase()} `$path` (operationId: $rawId)\n")
                    if (isDeprecated) append("@deprecated\n")
                }

                // Generate normal request
                val funSpec = FunSpec.builder(methodName)
                    .addModifiers(KModifier.SUSPEND)
                    .addKdoc(kdoc)
                    .returns(respType)
                    .apply {
                        // URL from correct base
                        addStatement("val url = %L", "\"$$baseVarName$path\"")

                        if (method == "post") {
                            if (bodyDto != null) {
                                // ----- POST WITH JSON BODY -----
                                addParameter("input", bodyDto)
                                if (isProxy) addStatement("if (authProxyConfigId.isNullOrBlank()) throw %T.MissingAuthProxyConfigId", errorClass)
                                addStatement("val bodyJson = json.encodeToString(%T.serializer(), input)", bodyDto)

                                if (isProxy) {
                                    addStatement(
                                        "val req = %T.Builder().url(url).post(bodyJson.%M(%S.%M())).header(%S, %N).header(%S, %S).build()",
                                        requestCls, toReqBody, "application/json", toMediaType,
                                        "X-Auth-Proxy-Config-ID", "authProxyConfigId",
                                        "X-Client-Version", clientVersionHdr
                                    )
                                } else {
                                    addStatement("val (hName, hValue) = stamper.stamp(bodyJson)")
                                    addStatement(
                                        "val req = %T.Builder().url(url).post(bodyJson.%M(%S.%M())).header(hName, hValue).header(%S, %S).build()",
                                        requestCls, toReqBody, "application/json", toMediaType,
                                        "X-Client-Version", clientVersionHdr
                                    )
                                }
                            } else {
                                // ----- POST WITH NO BODY (noop / anchor / ping) -----
                                addStatement("val bodyJson = %S", "{}")

                                if (isProxy) {
                                    addStatement(
                                        "val req = %T.Builder().url(url).post(bodyJson.%M(%S.%M())).header(%S, %N).header(%S, %S).build()",
                                        requestCls, toReqBody, "application/json", toMediaType,
                                        "X-Auth-Proxy-Config-ID", "authProxyConfigId",
                                        "X-Client-Version", clientVersionHdr
                                    )
                                } else {
                                    addStatement("val (hName, hValue) = stamper.stamp(bodyJson)")
                                    addStatement(
                                        "val req = %T.Builder().url(url).post(bodyJson.%M(%S.%M())).header(hName, hValue).header(%S, %S).build()",
                                        requestCls, toReqBody, "application/json", toMediaType,
                                        "X-Client-Version", clientVersionHdr
                                    )
                                }
                            }
                        } else {
                            if (isProxy) {
                                addStatement(
                                    "val req = %T.Builder().url(url).get().header(%S, %N).header(%S, %S).build()",
                                    requestCls,
                                    "X-Auth-Proxy-Config-ID", "authProxyConfigId",
                                    "X-Client-Version", clientVersionHdr
                                )
                            } else {
                                addStatement(
                                    "val req = %T.Builder().url(url).get().header(%S, %S).build()",
                                    requestCls,
                                    "X-Client-Version", clientVersionHdr
                                )
                            }
                        }

                        addStatement("val call = http.newCall(req)")
                        beginControlFlow("call.execute().use { resp ->")
                        beginControlFlow("if (!resp.isSuccessful)")
                        addStatement("throw RuntimeException(%P + resp.code)", "HTTP error from $path: ")
                        endControlFlow()
                        addStatement("val text = resp.body.string()")

                        if (respType == UNIT) {
                            addStatement("return Unit")
                        } else {
                            addStatement("return json.decodeFromString(%T.serializer(), text)", respType)
                        }
                        endControlFlow()
                    }
                    .build()
                typeBuilder.addFunction(funSpec)

                if (!isProxy) {
                    val tStampCls     = ClassName(typesPkg, "TStamp")
                    val tSignedReqCls = ClassName(typesPkg, "TSignedRequest")
                    val stampFunName  = "stamp" + methodName.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

                    val stampFunSpec = FunSpec.builder(stampFunName)
                        .addModifiers(KModifier.SUSPEND)
                        .returns(tSignedReqCls)
                        .apply {
                            // Build URL from the correct base (public or proxy base, but we only expose stamp for public)
                            addStatement("val url = %L", "\"$$baseVarName$path\"")

                            if (method == "post") {
                                if (bodyDto != null) {
                                    // POST with JSON body
                                    addParameter("input", bodyDto)
                                    addStatement("val bodyJson = json.encodeToString(%T.serializer(), input)", bodyDto)
                                } else {
                                    // POST with no body (noop)
                                    addStatement("val bodyJson = %S", "{}")
                                }
                                addStatement("val (hName, hValue) = stamper.stamp(bodyJson)")
                            } else {
                                // GET: if your stamp expects a digest of the body, decide what to sign.
                                // Here we stamp an empty string; adjust if you prefer to stamp the URL or query.
                                addStatement("val bodyJson = %S", "")
                                addStatement("val (hName, hValue) = stamper.stamp(bodyJson)")
                            }

                            addStatement("val stamp = %T(stampHeaderName = hName, stampHeaderValue = hValue)", tStampCls)
                            addStatement("return %T(body = bodyJson, stamp = stamp, url = url)", tSignedReqCls)
                        }
                        .build()

                    typeBuilder.addFunction(stampFunSpec)
                }
            }
        }
    }

    FileSpec.builder(pkg, className)
        .addType(typeBuilder.build())
        .build()
        .writeTo(outRoot)

    println("✅ Generated $pkg.$className (combined)")
}

/* ==========================
 *  Empty placeholder type generation
 * ========================== */
private fun generateEmptyModelsIfMissing(
    apis: List<Pair<SpecCfg, OpenAPI>>,
    outRoot: Path,
    modelPkg: String,
) {
    apis.forEach { (cfg, openAPI) ->
        val modelPrefix = cfg.prefix
        val fileBuilder = FileSpec.builder(
            modelPkg,
            "EmptySchemas${modelPrefix}Gen"
        ).addFileComment("@generated (empty schemas). DO NOT EDIT")

        var wrote = false
        val serializable = ClassName("kotlinx.serialization", "Serializable")

        openAPI.components?.schemas.orEmpty().forEach { (schemaName, schema) ->
            val isObject = (schema.type == "object" || (schema.type == null && schema.`$ref` == null))
            val isEmpty =
                isObject &&
                        schema.properties.isNullOrEmpty() &&
                        schema.allOf.isNullOrEmpty() &&
                        schema.oneOf.isNullOrEmpty() &&
                        schema.anyOf.isNullOrEmpty() &&
                        (schema.additionalProperties == null || schema.additionalProperties == false)

            if (!isEmpty) return@forEach

            val simpleName = modelPrefix + schemaName.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            val obj = TypeSpec.objectBuilder(simpleName)
                .addAnnotation(serializable)
                .addKdoc("Empty schema placeholder for `%L`.\n", schemaName)
                .build()

            fileBuilder.addType(obj)
            wrote = true
        }

        if (wrote) {
            fileBuilder.build().writeTo(outRoot)
            println("✅ Generated empty schema placeholders in $modelPkg (prefix=$modelPrefix)")
        }
    }
}

/* ==========================
 *  PublicApiTypes generation
 * ========================== */
private fun generateTypesFile(
    apis: List<Pair<SpecCfg, OpenAPI>>,
    outRoot: Path,
    typesPkg: String,
    modelPkg: String,
) {
    val fileBuilder = FileSpec.builder(typesPkg, "PublicApiTypes")
        .addFileComment("@generated by turnkey_codegen. DO NOT EDIT BY HAND")

    val serializable = ClassName("kotlinx.serialization", "Serializable")

    val stampCtor = FunSpec.constructorBuilder()
        .addParameter("stampHeaderName", STRING)
        .addParameter("stampHeaderValue", STRING)
        .build()

    val tStamp = TypeSpec.classBuilder("TStamp")
        .addModifiers(KModifier.DATA)
        .addAnnotation(AnnotationSpec.builder(serializable).build())
        .primaryConstructor(stampCtor)
        .addProperty(
            PropertySpec.builder("stampHeaderName", STRING)
                .initializer("stampHeaderName")
                .build()
        )
        .addProperty(
            PropertySpec.builder("stampHeaderValue", STRING)
                .initializer("stampHeaderValue")
                .build()
        )
        .build()

    fileBuilder.addType(tStamp)

    val tStampClass = ClassName(typesPkg, "TStamp")
    val signedReqCtor = FunSpec.constructorBuilder()
        .addParameter("body", STRING)
        .addParameter("stamp", tStampClass)
        .addParameter("url", STRING)
        .build()

    val tSignedRequest = TypeSpec.classBuilder("TSignedRequest")
        .addModifiers(KModifier.DATA)
        .addAnnotation(AnnotationSpec.builder(serializable).build())
        .primaryConstructor(signedReqCtor)
        .addProperty(PropertySpec.builder("body", STRING).initializer("body").build())
        .addProperty(PropertySpec.builder("stamp", tStampClass).initializer("stamp").build())
        .addProperty(PropertySpec.builder("url", STRING).initializer("url").build())
        .build()

    fileBuilder.addType(tSignedRequest)

    apis.forEach { (cfg, openAPI) ->
        val opPrefix = cfg.prefix // applied to type names for this spec

        openAPI.paths.orEmpty().forEach { (path, item) ->
            listOfNotNull("get" to item.get, "post" to item.post).forEach { (method, op) ->
                if (op == null) return@forEach

                val rawId = op.operationId?.takeIf { it.isNotBlank() } ?: return@forEach

                val isDeprecated = op.deprecated == true
                val kdocLine = "${method.uppercase()} `$path`"

                fun addKDocT(builder: TypeSpec.Builder): TypeSpec.Builder {
                    builder.addKdoc("$kdocLine\n")
                    if (isDeprecated) builder.addKdoc("@deprecated\n")
                    return builder
                }

                // ----- Response alias -----
                val respRef = op.responses["200"]?.content?.get("application/json")?.schema?.`$ref`
                val respTypeName: TypeName = respRef?.substringAfterLast("/")?.let { schema ->
                    ClassName(modelPkg, (opPrefix.ifBlank { "" }) + schema.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })
                } ?: UNIT

                fileBuilder.addTypeAlias(
                    TypeAliasSpec.builder("${opPrefix}T${rawId.substringAfter("_")}Response", respTypeName).build()
                )

                // ----- Body alias (POST only) -----
                val bodyRef = if (method == "post")
                    op.requestBody?.content?.get("application/json")?.schema?.`$ref` else null
                val bodyType = bodyRef?.substringAfterLast("/")?.let { schema ->
                    ClassName(modelPkg, (opPrefix.ifBlank { "" }) + schema.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })
                }
                val hasBody = bodyType != null
                if (hasBody) {
                    fileBuilder.addTypeAlias(
                        TypeAliasSpec.builder("${opPrefix}T${rawId.substringAfter("_")}Body", bodyType).build()
                    )
                }

                // ----- Query data class -----
                val queryParams = op.parameters?.filter { it.`in` == "query" }.orEmpty()
                val hasQuery = queryParams.isNotEmpty()
                if (hasQuery) {
                    val cls = TypeSpec.classBuilder("${opPrefix}T${rawId.substringAfter("_")}Query")
                    addKDocT(cls)
                    val ctor = FunSpec.constructorBuilder()
                    queryParams.forEach { p ->
                        val name = p.name
                        val required = p.required == true
                        val type = kotlinTypeFromSchema(p.schema, modelPkg, opPrefix)
                        val finalType = if (required) type else type.copy(nullable = true)
                        ctor.addParameter(ParameterSpec.builder(name, finalType).apply {
                            if (!required) defaultValue("null")
                        }.build())
                        cls.addProperty(PropertySpec.builder(name, finalType).initializer(name).build())
                    }
                    cls.primaryConstructor(ctor.build())
                    fileBuilder.addType(cls.build())
                }

                // ----- Substitution data class (path params) -----
                val pathParams = op.parameters?.filter { it.`in` == "path" }.orEmpty()
                val hasSubstitution = pathParams.isNotEmpty()
                if (hasSubstitution) {
                    val cls = TypeSpec.classBuilder("${opPrefix}T${rawId.substringAfter("_")}Substitution")
                    addKDocT(cls)
                    val ctor = FunSpec.constructorBuilder()
                    pathParams.forEach { p ->
                        val name = p.name
                        val type = kotlinTypeFromSchema(p.schema, modelPkg, opPrefix)
                        ctor.addParameter(name, type)
                        cls.addProperty(PropertySpec.builder(name, type).initializer(name).build())
                    }
                    cls.primaryConstructor(ctor.build())
                    fileBuilder.addType(cls.build())
                }
            }
        }
    }

    fileBuilder.build().writeTo(outRoot)
    println("✅ Generated $typesPkg.PublicApiTypes (combined)")
}
