import com.squareup.kotlinpoet.*
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.converter.SwaggerConverter
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import utils.*   // expects MAP, STRING, kotlinTypeFromSchema, etc.

/** One spec input + an optional prefix applied to operation/type names. */
data class SpecCfg(val path: Path, val prefix: String)

object VersionedActivityTypes {
    val map: Map<String, String> = mapOf(
        "ACTIVITY_TYPE_CREATE_AUTHENTICATORS" to "ACTIVITY_TYPE_CREATE_AUTHENTICATORS_V2",
        "ACTIVITY_TYPE_CREATE_API_KEYS" to "ACTIVITY_TYPE_CREATE_API_KEYS_V2",
        "ACTIVITY_TYPE_CREATE_POLICY" to "ACTIVITY_TYPE_CREATE_POLICY_V3",
        "ACTIVITY_TYPE_CREATE_PRIVATE_KEYS" to "ACTIVITY_TYPE_CREATE_PRIVATE_KEYS_V2",
        "ACTIVITY_TYPE_CREATE_SUB_ORGANIZATION" to "ACTIVITY_TYPE_CREATE_SUB_ORGANIZATION_V7",
        "ACTIVITY_TYPE_CREATE_USERS" to "ACTIVITY_TYPE_CREATE_USERS_V3",
        "ACTIVITY_TYPE_SIGN_RAW_PAYLOAD" to "ACTIVITY_TYPE_SIGN_RAW_PAYLOAD_V2",
        "ACTIVITY_TYPE_SIGN_TRANSACTION" to "ACTIVITY_TYPE_SIGN_TRANSACTION_V2",
        "ACTIVITY_TYPE_EMAIL_AUTH" to "ACTIVITY_TYPE_EMAIL_AUTH_V2",
        "ACTIVITY_TYPE_CREATE_READ_WRITE_SESSION" to "ACTIVITY_TYPE_CREATE_READ_WRITE_SESSION_V2",
        "ACTIVITY_TYPE_UPDATE_POLICY" to "ACTIVITY_TYPE_UPDATE_POLICY_V2",
        "ACTIVITY_TYPE_INIT_OTP_AUTH" to "ACTIVITY_TYPE_INIT_OTP_AUTH_V2",
    )

    /** Fallbacks to the input if there’s no versioned entry. */
    fun resolve(type: String): String = map[type] ?: type
}

private fun Schema<*>.refName(): String? = this.`$ref`?.substringAfterLast("/")

private fun flattenAllOf(
    schema: Schema<*>,
    components: Map<String, Schema<*>>,
): Pair<Map<String, Schema<*>>, Set<String>> {
    val props = linkedMapOf<String, Schema<*>>()
    val required = linkedSetOf<String>()

    fun absorb(s: Schema<*>) {
        s.properties?.forEach { (k, v) -> props[k] = v as Schema<*> }
        s.required?.forEach { required += it }
    }

    if (schema.allOf?.isNotEmpty() == true) {
        schema.allOf.forEach { part ->
            val resolved = part.refName()?.let { components[it] } ?: part
            absorb(resolved)
        }
    } else {
        absorb(schema)
    }
    return props to required
}

/** Pick latest Intent name by suffix V<digits>, fall back to exact if no versions. */
private fun latestIntentName(base: String, schemas: Map<String, Schema<*>>?): String? {
    if (schemas == null) return null
    if (schemas.containsKey(base)) return base
    val re = Regex("^${Regex.escape(base)}V(\\d+)$")
    return schemas.keys
        .mapNotNull { k ->
            re.matchEntire(k)?.groupValues?.getOrNull(1)?.toIntOrNull()?.let { v -> k to v }
        }
        .maxByOrNull { it.second }
        ?.first
}


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
    val modelPkg = arg("--modelPkg")
    val clientClass = arg("--class", "TurnkeyClient")
    val typesPkg = arg("--typesPkg", pkg)
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
    val errorClass = ClassName("com.turnkey.http.utils", "TurnkeyHttpErrors")
    val okHttpClient = ClassName("okhttp3", "OkHttpClient")
    val requestCls = ClassName("okhttp3", "Request")
    val toMediaType = MemberName("okhttp3.MediaType.Companion", "toMediaType")
    val toReqBody = MemberName("okhttp3.RequestBody.Companion", "toRequestBody")
    val jsonCls = ClassName("kotlinx.serialization.json", "Json")
    val jsonObject = MemberName("kotlinx.serialization.json", "jsonObject")
    val jsonPrimitive = MemberName("kotlinx.serialization.json", "jsonPrimitive")

    val stringT = String::class.asTypeName()
    val nullableStringT = stringT.copy(nullable = true)

    val ctor = FunSpec.constructorBuilder()
        .addParameter(
            ParameterSpec.builder("apiBaseUrl", nullableStringT)
                .defaultValue("null")
                .build()
        )
        .addParameter("stamper", stamperClass.copy(nullable = true))
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
            PropertySpec.builder("stamper", stamperClass.copy(nullable = true), KModifier.PRIVATE)
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
                val methodName =
                    if (isProxy) opPrefix.lowercase() + opId else opId.replaceFirstChar { it.lowercaseChar() }

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
                        val kind = classifyOperation(methodName, path)

                        if (kind == OperationKind.Query) {
                            // ----- POST WITH JSON BODY -----
                            addParameter("input", bodyDto!!)
                            if (isProxy) addStatement(
                                "if (authProxyConfigId.isNullOrBlank()) throw %T.MissingAuthProxyConfigId",
                                errorClass
                            ) else addStatement(
                                "if (stamper == null) throw %T.StamperNotInitialized", errorClass
                            )
                            addStatement(
                                "val bodyJson = json.encodeToString(%T.serializer(), input)",
                                bodyDto
                            )

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
                        } else if (kind == OperationKind.Activity || kind == OperationKind.ActivityDecision) {
                            // ----- POST WITH JSON BODY -----
                            addParameter("input", bodyDto!!)
                            if (isProxy) addStatement(
                                "if (authProxyConfigId.isNullOrBlank()) throw %T.MissingAuthProxyConfigId",
                                errorClass
                            ) else addStatement(
                                "if (stamper == null) throw %T.StamperNotInitialized",
                                errorClass
                            )

                            if (isProxy) {
                                // proxy unchanged
                                addStatement(
                                    "val bodyJson = json.encodeToString(%T.serializer(), input)",
                                    bodyDto
                                )
                                addStatement(
                                    "val req = %T.Builder().url(url).post(bodyJson.%M(%S.%M())).header(%S, %N).header(%S, %S).build()",
                                    requestCls, toReqBody, "application/json", toMediaType,
                                    "X-Auth-Proxy-Config-ID", "authProxyConfigId",
                                    "X-Client-Version", clientVersionHdr
                                )
                            } else {
                                // ------ PUBLIC: build activity envelope ------
                                addStatement(
                                    "val inputElem = json.encodeToJsonElement(%T.serializer(), input)",
                                    bodyDto
                                )
                                addStatement("val obj = inputElem.%M", jsonObject)

                                // extract organizationId and timestampMs if present
                                addStatement("val orgIdElem = obj[%S]", "organizationId")
                                addStatement("val tsElem = obj[%S]", "timestampMs")

                                // parameters = all fields except organizationId/timestampMs
                                addStatement(
                                    "val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != %S && k != %S) put(k, v) } }",
                                    "organizationId",
                                    "timestampMs"
                                )

                                // timestamp fallback to now
                                addStatement(
                                    "val ts = tsElem?.%M?.content ?: System.currentTimeMillis().toString()",
                                    jsonPrimitive
                                )

                                // type = ACTIVITY_TYPE_<OP_ID in SNAKE>
                                val snake = rawId.substringAfter("_").toScreamingSnake()
                                val versioned = VersionedActivityTypes.resolve("ACTIVITY_TYPE_$snake")
                                addStatement("val activityType = %S", versioned)

                                // compose final body
                                addStatement(
                                    "val bodyObj = kotlinx.serialization.json.buildJsonObject { " +
                                            "put(%S, params); " +                                        // parameters
                                            "orgIdElem?.let { put(%S, it) }; " +                          // organizationId (optional)
                                            "put(%S, kotlinx.serialization.json.JsonPrimitive(ts)); " +    // timestampMs
                                            "put(%S, kotlinx.serialization.json.JsonPrimitive(activityType)) " + // type
                                            "}",
                                    "parameters", "organizationId", "timestampMs", "type"
                                )
                                addStatement("val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)")

                                // stamp & request
                                addStatement("val (hName, hValue) = stamper.stamp(bodyJson)")
                                addStatement(
                                    "val req = %T.Builder().url(url).post(bodyJson.%M(%S.%M())).header(hName, hValue).header(%S, %S).build()",
                                    requestCls, toReqBody, "application/json", toMediaType,
                                    "X-Client-Version", clientVersionHdr
                                )
                            }
                        } else {
                            // ----- POST WITH NO BODY (noop / anchor / ping) -----
                            addStatement(
                                "if (stamper == null) throw %T.StamperNotInitialized",
                                errorClass
                            )
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

                        addStatement("val call = http.newCall(req)")
                        beginControlFlow("call.execute().use { resp ->")
                        beginControlFlow("if (!resp.isSuccessful)")
                        addStatement(
                            "throw RuntimeException(%P + resp.code)",
                            "HTTP error from $path: "
                        )
                        endControlFlow()
                        addStatement("val text = resp.body?.string() ?: throw RuntimeException(%P)", "Empty response body from $path")

                        if (respType == UNIT) {
                            addStatement("return Unit")
                        } else {
                            addStatement(
                                "return json.decodeFromString(%T.serializer(), text)",
                                respType
                            )
                        }
                        endControlFlow()
                    }
                    .build()
                typeBuilder.addFunction(funSpec)

                if (!isProxy) {
                    val tStampCls = ClassName(typesPkg, "TStamp")
                    val tSignedReqCls = ClassName(typesPkg, "TSignedRequest")
                    val stampFunName = "stamp" + methodName.capitalizeLeading()

                    val stampFunSpec = FunSpec.builder(stampFunName)
                        .addModifiers(KModifier.SUSPEND)
                        .returns(tSignedReqCls)
                        .apply {
                            addStatement(
                                "if (stamper == null) throw %T.StamperNotInitialized",
                                errorClass
                            )
                            // Build URL from the correct base (public or proxy base, but we only expose stamp for public)
                            addStatement("val url = %L", "\"$$baseVarName$path\"")
                            val kind = classifyOperation(methodName, path)

                            if (kind == OperationKind.Query) {
                                if (bodyDto != null) {
                                    // POST with JSON body
                                    addParameter("input", bodyDto)
                                    addStatement(
                                        "val bodyJson = json.encodeToString(%T.serializer(), input)",
                                        bodyDto
                                    )
                                } else {
                                    // POST with no body (noop)
                                    addStatement("val bodyJson = %S", "{}")
                                }
                                addStatement("val (hName, hValue) = stamper.stamp(bodyJson)")
                            } else if (kind == OperationKind.Activity || kind == OperationKind.ActivityDecision) {
                                addParameter("input", bodyDto!!)
                                // ------ PUBLIC: build activity envelope ------
                                addStatement(
                                    "val inputElem = json.encodeToJsonElement(%T.serializer(), input)",
                                    bodyDto
                                )
                                addStatement("val obj = inputElem.%M", jsonObject)

                                // extract organizationId and timestampMs if present
                                addStatement("val orgIdElem = obj[%S]", "organizationId")
                                addStatement("val tsElem = obj[%S]", "timestampMs")

                                // parameters = all fields except organizationId/timestampMs
                                addStatement(
                                    "val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != %S && k != %S) put(k, v) } }",
                                    "organizationId",
                                    "timestampMs"
                                )

                                // timestamp fallback to now
                                addStatement(
                                    "val ts = tsElem?.%M?.content ?: System.currentTimeMillis().toString()",
                                    jsonPrimitive
                                )

                                // type = ACTIVITY_TYPE_<OP_ID in SNAKE>
                                val snake = rawId.substringAfter("_").toScreamingSnake()
                                val versioned = VersionedActivityTypes.resolve("ACTIVITY_TYPE_$snake")
                                addStatement("val activityType = %S", versioned)

                                // compose final body
                                addStatement(
                                    "val bodyObj = kotlinx.serialization.json.buildJsonObject { " +
                                            "put(%S, params); " +                                        // parameters
                                            "orgIdElem?.let { put(%S, it) }; " +                          // organizationId (optional)
                                            "put(%S, kotlinx.serialization.json.JsonPrimitive(ts)); " +    // timestampMs
                                            "put(%S, kotlinx.serialization.json.JsonPrimitive(activityType)) " + // type
                                            "}",
                                    "parameters", "organizationId", "timestampMs", "type"
                                )
                                addStatement("val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)")
                                addStatement("val (hName, hValue) = stamper.stamp(bodyJson)")
                            } else if (kind == OperationKind.Noop) {
                                addStatement("val bodyJson = %S", "")
                                addStatement("val (hName, hValue) = stamper.stamp(bodyJson)")
                            }

                            addStatement(
                                "val stamp = %T(stampHeaderName = hName, stampHeaderValue = hValue)",
                                tStampCls
                            )
                            addStatement(
                                "return %T(body = bodyJson, stamp = stamp, url = url)",
                                tSignedReqCls
                            )
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
            val isObject =
                (schema.type == "object" || (schema.type == null && schema.`$ref` == null))
            val isEmpty =
                isObject &&
                        schema.properties.isNullOrEmpty() &&
                        schema.allOf.isNullOrEmpty() &&
                        schema.oneOf.isNullOrEmpty() &&
                        schema.anyOf.isNullOrEmpty() &&
                        (schema.additionalProperties == null || schema.additionalProperties == false)

            if (!isEmpty) return@forEach

            val simpleName = modelPrefix + schemaName.capitalizeLeading()
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
                    ClassName(modelPkg, (opPrefix.ifBlank { "" }) + schema.capitalizeLeading())
                } ?: UNIT

                fileBuilder.addTypeAlias(
                    TypeAliasSpec.builder(
                        "${opPrefix}T${rawId.substringAfter("_")}Response",
                        respTypeName
                    ).build()
                )

                // ----- Body as inline Intent fields + optional organizationId (POST only) -----
                if (method == "post") {
                    val requestRef =
                        op.requestBody?.content?.get("application/json")?.schema?.`$ref`
                    val requestSchemaName = requestRef?.substringAfterLast("/")

                    if (requestSchemaName != null) {
                        val schemas = openAPI.components?.schemas.orEmpty()
                        val requestSchema = schemas[requestSchemaName]

                        // Request.parameters.$ref -> Intent base
                        val parametersSchemaRef =
                            requestSchema?.properties?.get("parameters")?.refName()
                        val intentSchema = parametersSchemaRef?.let { schemas[it] }

                        if (intentSchema != null) {
                            // Flatten intent properties/required
                            val (intentProps, intentRequired) = flattenAllOf(intentSchema, schemas)

                            // Build **Body** class = inline intent props + optional organizationId (non-proxy only)
                            val bodyClassName = "${opPrefix}T${rawId.substringAfter("_")}Body"
                            val cls = TypeSpec.classBuilder(bodyClassName)
                                .addAnnotation(kotlinx.serialization.Serializable::class)
                                .apply { addKDocT(this) }

                            val ctor = FunSpec.constructorBuilder()

                            // Non-proxy specs get optional orgId
                            if (opPrefix.isBlank()) {
                                val t = String::class.asTypeName().copy(nullable = true)
                                ctor.addParameter(
                                    ParameterSpec.builder("organizationId", String::class)
                                        .build()
                                )
                                ctor.addParameter(
                                    ParameterSpec.builder("timestampMs", t)
                                        .defaultValue("null")
                                        .build()
                                )
                                cls.addProperty(
                                    PropertySpec.builder("organizationId", String::class)
                                        .initializer("organizationId")
                                        .build()
                                )
                                cls.addProperty(
                                    PropertySpec.builder("timestampMs", t)
                                        .initializer("timestampMs")
                                        .build()
                                )
                            }

                            // Inline **all** intent properties directly into Body
                            intentProps.forEach { (propName, propSchema) ->
                                val isRequired = propName in intentRequired
                                val ktType = kotlinTypeFromSchema(propSchema, modelPkg, opPrefix)
                                val finalType =
                                    if (isRequired) ktType else ktType.copy(nullable = true)

                                // constructor param
                                val paramBuilder = ParameterSpec.builder(propName, finalType)
                                if (!isRequired) paramBuilder.defaultValue("null")
                                ctor.addParameter(paramBuilder.build())

                                // property
                                cls.addProperty(
                                    PropertySpec.builder(propName, finalType)
                                        .initializer(propName)
                                        .build()
                                )
                            }

                            cls.primaryConstructor(ctor.build())
                            fileBuilder.addType(cls.build())
                        } else {
                            val bodyRef =
                                op.requestBody?.content?.get("application/json")?.schema?.`$ref`
                            val bodyType = bodyRef?.substringAfterLast("/")?.let { schema ->
                                ClassName(
                                    modelPkg,
                                    (opPrefix.ifBlank { "" }) + schema.capitalizeLeading()
                                )
                            }
                            val hasBody = bodyType != null
                            if (hasBody) {
                                fileBuilder.addTypeAlias(
                                    TypeAliasSpec.builder(
                                        "${opPrefix}T${rawId.substringAfter("_")}Body",
                                        bodyType
                                    ).build()
                                )
                            }
                        }
                    }
                }

                //----- Request type alias -----
                val bodyRef = op.requestBody?.content?.get("application/json")?.schema?.`$ref`
                val requestType = bodyRef?.substringAfterLast("/")?.let { schema ->
                    ClassName(modelPkg, (opPrefix.ifBlank { "" }) + schema.capitalizeLeading())
                }
                val hasRequest = requestType != null
                if (hasRequest) {
                    fileBuilder.addTypeAlias(
                        TypeAliasSpec.builder(
                            "${opPrefix}T${rawId.substringAfter("_")}Request",
                            requestType
                        ).build()
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
                        cls.addProperty(
                            PropertySpec.builder(name, finalType).initializer(name).build()
                        )
                    }
                    cls.primaryConstructor(ctor.build())
                    fileBuilder.addType(cls.build())
                }

                // ----- Substitution data class (path params) -----
                val pathParams = op.parameters?.filter { it.`in` == "path" }.orEmpty()
                val hasSubstitution = pathParams.isNotEmpty()
                if (hasSubstitution) {
                    val cls =
                        TypeSpec.classBuilder("${opPrefix}T${rawId.substringAfter("_")}Substitution")
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
