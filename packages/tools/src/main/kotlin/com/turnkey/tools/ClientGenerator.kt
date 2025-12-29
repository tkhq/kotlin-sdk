package com.turnkey.tools

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.asTypeName
import com.turnkey.tools.utils.GENERATED_FILE_HEADER
import com.turnkey.tools.utils.OperationKind
import com.turnkey.tools.utils.SpecCfg
import com.turnkey.tools.utils.VersionedActivityTypes
import com.turnkey.tools.utils.capitalizeLeading
import com.turnkey.tools.utils.classifyOperation
import com.turnkey.tools.utils.definitions
import com.turnkey.tools.utils.extractLatestVersions
import com.turnkey.tools.utils.findProjectRoot
import com.turnkey.tools.utils.readJson
import com.turnkey.tools.utils.toScreamingSnake
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.converter.SwaggerConverter
import kotlinx.serialization.json.JsonObject
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories

/** Entry point for generating Turnkey HTTP client from OpenAPI specs */
fun main(args: Array<String>) {
    val argv = args.toList()
    fun arg(name: String, default: String? = null): String =
        argv.getOrNull(argv.indexOf(name) + 1) ?: default
        ?: error("Missing $name")

    // Find project root (contains openapi/ directory)
    val projectRoot = findProjectRoot()
    
    // Hardcoded conventions for Turnkey API specs
    val specs = listOf(
        SpecCfg(projectRoot.resolve("openapi/public_api.swagger.json"), prefix = ""),
        SpecCfg(projectRoot.resolve("openapi/auth_proxy.swagger.json"), prefix = "Proxy")
    )
    
    // Configurable parameters
    val outRoot = Path.of(arg("--out"))
    val pkg = arg("--pkg")
    val modelsPkg = arg("--models-pkg")
    val clientClass = arg("--client-class-name", "TurnkeyClient")

    specs.forEach { require(Files.exists(it.path)) { "Spec not found: ${it.path}" } }
    outRoot.createDirectories()

    // Parse all specs → OpenAPI 3
    val apis = specs.map { s -> Triple(s, parseToOpenApi3(s.path), readJson(s.path)) }
    generateClientFile(apis, outRoot, pkg, modelsPkg, clientClass)
}

/** Convert Swagger 2.0 to OpenAPI 3 for easier traversal. */
private fun parseToOpenApi3(spec: Path): OpenAPI {
    val opts = ParseOptions().apply { isResolve = true; isFlatten = true }
    return SwaggerConverter().readLocation(spec.toString(), null, opts).openAPI
        ?: error("Failed to parse/convert spec: $spec")
}

fun generateClientFile(
    apis: List<Triple<SpecCfg, OpenAPI, JsonObject>>,
    outRoot: Path,
    pkg: String,
    modelsPkg: String,
    className: String,
) {
    val stamperClass = ClassName("com.turnkey.stamper", "Stamper")
    val errorClass = ClassName("com.turnkey.http.utils", "TurnkeyHttpError")
    val okHttpClient = ClassName("okhttp3", "OkHttpClient")
    val requestCls = ClassName("okhttp3", "Request")
    val toMediaType = MemberName("okhttp3.MediaType.Companion", "toMediaType")
    val toReqBody = MemberName("okhttp3.RequestBody.Companion", "toRequestBody")
    val jsonCls = ClassName("kotlinx.serialization.json", "Json")
    val jsonObject = MemberName("kotlinx.serialization.json", "jsonObject")
    val jsonPrimitive = MemberName("kotlinx.serialization.json", "jsonPrimitive")

    val awaitM = MemberName("", "await")
    val suspendCancellable = MemberName("kotlinx.coroutines", "suspendCancellableCoroutine")
    val withContextM = MemberName("kotlinx.coroutines", "withContext")
    val dispatchersCls = ClassName("kotlinx.coroutines", "Dispatchers")
    val resumeM = MemberName("kotlin.coroutines", "resume")
    val resumeWithExceptionM = MemberName("kotlin.coroutines", "resumeWithException")

    val okCallCls = ClassName("okhttp3", "Call")
    val okCallbackCls = ClassName("okhttp3", "Callback")
    val okResponseCls = ClassName("okhttp3", "Response")
    val ioExceptionCls = ClassName("java.io", "IOException")
    val clientVersionHdr = ClassName("com.turnkey.http", "Version")

    val activityResponseCls = ClassName("com.turnkey.types", "V1ActivityResponse")
    val activityCls = ClassName("com.turnkey.types", "V1Activity")

    val stringT = String::class.asTypeName()
    val nullableStringT = stringT.copy(nullable = true)

    val ctor = FunSpec.constructorBuilder()
        .addParameter(
            ParameterSpec.builder("apiBaseUrl", nullableStringT)
                .defaultValue("null")
                .build()
        )
        .addParameter("stamper", stamperClass.copy(nullable = true))
        .addParameter(
            ParameterSpec.builder("http", okHttpClient.copy(nullable = true))
                .defaultValue("null")
                .build()
        )
        .addParameter(
            ParameterSpec.builder("authProxyUrl", nullableStringT)
                .defaultValue("null")
                .build()
        )
        .addParameter(
            ParameterSpec.builder("authProxyConfigId", nullableStringT).defaultValue("null").build()
        )
        .addParameter("organizationId", stringT)
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
                .initializer("%N ?: %T()", "http", okHttpClient)
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
            PropertySpec.builder("organizationId", stringT, KModifier.PRIVATE)
                .initializer("%N", "organizationId")
                .build()
        )
        .addProperty(
            PropertySpec.builder("json", jsonCls, KModifier.PRIVATE)
                .initializer("%T { ignoreUnknownKeys = true }", jsonCls)
                .build()
        )

    typeBuilder.addFunction(
        FunSpec.builder("await")
            .addModifiers(KModifier.PRIVATE, KModifier.SUSPEND)
            .receiver(okCallCls)
            .returns(okResponseCls)
            .addCode(
                """
            return %M { cont ->
                this@await.enqueue(object : %T {
                    override fun onFailure(call: %T, e: %T) {
                        if (!cont.isCompleted) cont.%M(e)
                    }
                    override fun onResponse(call: %T, response: %T) {
                        if (!cont.isCompleted) cont.%M(response)
                    }
                })
                cont.invokeOnCancellation { kotlin.runCatching { cancel() }.getOrNull() }
            }
            """.trimIndent(),
                suspendCancellable, okCallbackCls, okCallCls, ioExceptionCls, resumeWithExceptionM,
                okCallCls, okResponseCls, resumeM
            )
            .build()
    )

    // Generate the reified activity helper function
    typeBuilder.addFunction(
        FunSpec.builder("activity")
            .addModifiers(KModifier.PRIVATE, KModifier.SUSPEND, KModifier.INLINE)
            .addTypeVariable(
                com.squareup.kotlinpoet.TypeVariableName.invoke("TBodyType").copy(reified = true)
            )
            .addParameter("url", String::class)
            .addParameter("body", com.squareup.kotlinpoet.TypeVariableName.invoke("TBodyType"))
            .addParameter("activityType", String::class)
            .returns(activityCls)
            .addCode(
                """
                if (stamper == null) throw %T.StamperNotInitialized()

                val inputJson = json.encodeToJsonElement(kotlinx.serialization.serializer<TBodyType>(), body)
                val obj = inputJson.%M
                val inputOrgId = obj["organizationId"]
                val inputTimestamp = obj["timestampMs"]

                val params = kotlinx.serialization.json.buildJsonObject {
                    obj.forEach { (k, v) ->
                        if (k != "organizationId" && k != "timestampMs") put(k, v)
                    }
                }
                val ts = inputTimestamp?.%M?.content ?: System.currentTimeMillis().toString()
                
                // Use provided organizationId from body, or fall back to client's organizationId
                val finalOrgId = inputOrgId ?: kotlinx.serialization.json.JsonPrimitive(organizationId)

                val bodyObj = kotlinx.serialization.json.buildJsonObject {
                    put("parameters", params)
                    finalOrgId?.let { put("organizationId", it) }
                    put("timestampMs", kotlinx.serialization.json.JsonPrimitive(ts))
                    put("type", kotlinx.serialization.json.JsonPrimitive(activityType))
                }
                val bodyJson = json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), bodyObj)
                val (hName, hValue) = stamper.stamp(bodyJson)

                val req = %T.Builder()
                    .url(url)
                    .post(bodyJson.%M("application/json".%M()))
                    .header(hName, hValue)
                    .header("X-Client-Version", %T.VERSION)
                    .build()

                val resp = http.newCall(req).%M()

                return resp.use {
                    if (!it.isSuccessful) {
                        val errBody = %M(%T.IO) {
                            kotlin.runCatching { it.body.string() }.getOrNull()
                        }
                        throw RuntimeException("HTTP error calling ${'$'}activityType request\nError: ${'$'}errBody\nCode: ${'$'}{it.code}")
                    }
                    val text = %M(%T.IO) { it.body.string() }
                    json.decodeFromString<%T>(text).activity
                }
                """.trimIndent(),
                errorClass, jsonObject, jsonPrimitive, requestCls, toReqBody, toMediaType, clientVersionHdr,
                awaitM, withContextM, dispatchersCls, withContextM, dispatchersCls, activityResponseCls
            )
            .build()
    )

    // For each spec and each operation, generate a method.
    apis.forEach { (cfg, openAPI, swagger) ->
        val opPrefix = cfg.prefix
        val baseVarName = if (opPrefix.isBlank()) "apiBaseUrl" else "authProxyUrl"
        val isProxy = !opPrefix.isBlank()
        val defs = swagger.definitions()
        val latestVersions = extractLatestVersions(defs)

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
                    ClassName(modelsPkg, typeName)
                }

                // Response type (or Unit)
                val respType: TypeName = respSchemaRef?.let { ref ->
                    val schemaName = opId.substringAfter("_")
                    val typeName = (opPrefix.ifBlank { "" }) + "T" + schemaName + "Response"
                    ClassName(modelsPkg, typeName)
                } ?: UNIT

                // Generate normal request
                val funSpec = FunSpec.builder(methodName)
                    .addModifiers(KModifier.SUSPEND)
                    .returns(respType)
                    .apply {
                        // URL from correct base
                        addStatement("val url = %L", "\"$$baseVarName$path\"")
                        val kind = classifyOperation(methodName, path)

                        if (kind == OperationKind.Query) {
                            // ----- POST WITH JSON BODY -----
                            addParameter("input", bodyDto!!)
                            if (isProxy) addStatement(
                                "if (authProxyConfigId.isNullOrBlank()) throw %T.MissingAuthProxyConfigId()",
                                errorClass
                            ) else addStatement(
                                "if (stamper == null) throw %T.StamperNotInitialized()", errorClass
                            )
                            
                            // Add organizationId fallback logic for queries
                            if (!isProxy) {
                                addStatement(
                                    "val bodyJson = json.encodeToJsonElement(%T.serializer(), input).%M.let { obj -> kotlinx.serialization.json.buildJsonObject { obj.filterKeys { it != %S }.forEach { (k, v) -> put(k, v) }; put(%S, obj[%S] ?: kotlinx.serialization.json.JsonPrimitive(organizationId)) } }.let { json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), it) }",
                                    bodyDto, jsonObject, "organizationId", "organizationId", "organizationId"
                                )
                            } else {
                                addStatement(
                                    "val bodyJson = json.encodeToString(%T.serializer(), input)",
                                    bodyDto
                                )
                            }

                            if (isProxy) {
                                addStatement(
                                    "val req = %T.Builder().url(url).post(bodyJson.%M(%S.%M())).header(%S, %N).header(%S, %T.VERSION).build()",
                                    requestCls, toReqBody, "application/json", toMediaType,
                                    "X-Auth-Proxy-Config-ID", "authProxyConfigId",
                                    "X-Client-Version", clientVersionHdr
                                )
                            } else {
                                addStatement("val (hName, hValue) = stamper.stamp(bodyJson)")
                                addStatement(
                                    "val req = %T.Builder().url(url).post(bodyJson.%M(%S.%M())).header(hName, hValue).header(%S, %T.VERSION).build()",
                                    requestCls, toReqBody, "application/json", toMediaType,
                                    "X-Client-Version", clientVersionHdr
                                )
                            }
                        } else if (kind == OperationKind.Activity || kind == OperationKind.ActivityDecision) {
                            // ----- POST WITH JSON BODY -----
                            addParameter("input", bodyDto!!)

                            if (isProxy) {
                                addStatement(
                                    "if (authProxyConfigId.isNullOrBlank()) throw %T.MissingAuthProxyConfigId()",
                                    errorClass
                                )
                                // proxy unchanged
                                addStatement(
                                    "val bodyJson = json.encodeToString(%T.serializer(), input)",
                                    bodyDto
                                )
                                addStatement(
                                    "val req = %T.Builder().url(url).post(bodyJson.%M(%S.%M())).header(%S, %N).header(%S, %T.VERSION).build()",
                                    requestCls, toReqBody, "application/json", toMediaType,
                                    "X-Auth-Proxy-Config-ID", "authProxyConfigId",
                                    "X-Client-Version", clientVersionHdr
                                )
                            } else {
                                // ------ PUBLIC: use activity helper ------
                                val activityResultType = respSchemaRef?.let { ref ->
                                    val schemaName = opId.substringAfter("_")

                                    val snake = schemaName.toScreamingSnake()
                                    val versioned =
                                        VersionedActivityTypes.map["ACTIVITY_TYPE_$snake"]

                                    // search for intents matching the above version
                                    val candidate = versioned?.let { v ->
                                        defs.keys.firstOrNull { k ->
                                            k == v.third
                                        }
                                    }

                                    val resultName =
                                        candidate
                                            ?.removePrefix("v1")
                                            ?.replaceFirstChar { it.lowercase() }
                                            ?: latestVersions["${schemaName}Result"]?.formattedKeyName
                                    "${opPrefix.ifBlank { "" }}$resultName"
                                }
                                val snake = rawId.substringAfter("_").toScreamingSnake()
                                val versioned = VersionedActivityTypes.resolve("ACTIVITY_TYPE_$snake")
                                addStatement("val activityType = %S", versioned)
                                addStatement("val activityRes = activity<%T>(url, input, activityType)", bodyDto)

                                if (kind == OperationKind.Activity) addStatement("return %T(activity = activityRes, result = activityRes.result.$activityResultType ?: throw RuntimeException(\"No result found from $path\"))", respType)
                                else addStatement("return %T(activity = activityRes)", respType)
                            }
                        } else {
                            // ----- POST WITH NO BODY (noop / anchor / ping) -----
                            addStatement(
                                "if (stamper == null) throw %T.StamperNotInitialized()",
                                errorClass
                            )
                            addStatement("val bodyJson = %S", "{}")

                            if (isProxy) {
                                addStatement(
                                    "val req = %T.Builder().url(url).post(bodyJson.%M(%S.%M())).header(%S, %N).header(%S, %T.VERSION).build()",
                                    requestCls, toReqBody, "application/json", toMediaType,
                                    "X-Auth-Proxy-Config-ID", "authProxyConfigId",
                                    "X-Client-Version", clientVersionHdr
                                )
                            } else {
                                addStatement("val (hName, hValue) = stamper.stamp(bodyJson)")
                                addStatement(
                                    "val req = %T.Builder().url(url).post(bodyJson.%M(%S.%M())).header(hName, hValue).header(%S, %T.VERSION).build()",
                                    requestCls, toReqBody, "application/json", toMediaType,
                                    "X-Client-Version", clientVersionHdr
                                )
                            }
                        }

                        if (isProxy || kind != OperationKind.Activity && kind != OperationKind.ActivityDecision) {
                            addStatement("val call = http.newCall(req)")
                            addStatement("val resp = call.%M()", awaitM)
                            beginControlFlow("resp.use {")
                            beginControlFlow("if (!it.isSuccessful)")
                            addStatement(
                                "val errBody = %M(%T.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }",
                                withContextM, dispatchersCls
                            )
                            addStatement(
                                "throw RuntimeException(%P + it.code)",
                                "HTTP error from $path: "
                            )
                            endControlFlow()
                            addStatement(
                                "val text = %M(%T.IO) { it.body.string() }",
                                withContextM, dispatchersCls
                            )
                            if (respType == UNIT) {
                                addStatement("return Unit")
                            } else if (kind == OperationKind.Activity && !isProxy) {
                                val activityResultType = respSchemaRef?.let { ref ->
                                    val schemaName = opId.substringAfter("_")

                                    val snake = schemaName.toScreamingSnake()
                                    val versioned =
                                        VersionedActivityTypes.map["ACTIVITY_TYPE_$snake"]

                                    // search for intents matching the above version
                                    val candidate = versioned?.let { v ->
                                        defs.keys.firstOrNull { k ->
                                            k == v.third
                                        }
                                    }

                                    val resultName =
                                        candidate
                                            ?.removePrefix("v1")
                                            ?.replaceFirstChar { it.lowercase() }
                                            ?: latestVersions["${schemaName}Result"]?.formattedKeyName
                                    "${opPrefix.ifBlank { "" }}$resultName"
                                }

                                addStatement(
                                    "val response = json.decodeFromString(%T.serializer(), text)",
                                    activityResponseCls
                                )
                                addStatement("val result = response.activity.result.$activityResultType ?: throw RuntimeException(\"No result found from $path\")")
                                addStatement(
                                    "return %T(activity = response.activity, result = result)",
                                    respType
                                )
                            } else {
                                addStatement(
                                    "return json.decodeFromString(%T.serializer(), text)",
                                    respType
                                )
                            }
                            endControlFlow()
                        }
                    }
                    .build()
                typeBuilder.addFunction(funSpec)

                if (!isProxy) {
                    val tStampCls = ClassName(modelsPkg, "TStamp")
                    val tSignedReqCls = ClassName(modelsPkg, "TSignedRequest")
                    val stampFunName = "stamp" + methodName.capitalizeLeading()

                    val stampFunSpec = FunSpec.builder(stampFunName)
                        .addModifiers(KModifier.SUSPEND)
                        .returns(tSignedReqCls)
                        .apply {
                            addStatement(
                                "if (stamper == null) throw %T.StamperNotInitialized()",
                                errorClass
                            )
                            // Build URL from the correct base (public or proxy base, but we only expose stamp for public)
                            addStatement("val url = %L", "\"$$baseVarName$path\"")
                            val kind = classifyOperation(methodName, path)

                            if (kind == OperationKind.Query) {
                                if (bodyDto != null) {
                                    // POST with JSON body
                                    addParameter("input", bodyDto)
                                    // Add organizationId fallback for stamp queries
                                    addStatement(
                                        "val bodyJson = json.encodeToJsonElement(%T.serializer(), input).%M.let { obj -> kotlinx.serialization.json.buildJsonObject { obj.filterKeys { it != %S }.forEach { (k, v) -> put(k, v) }; put(%S, obj[%S] ?: kotlinx.serialization.json.JsonPrimitive(organizationId)) } }.let { json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), it) }",
                                        bodyDto, jsonObject, "organizationId", "organizationId", "organizationId"
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
                                addStatement("val inputOrgId = obj[%S]", "organizationId")
                                addStatement("val inputTimestamp = obj[%S]", "timestampMs")

                                // parameters = all fields except organizationId/timestampMs
                                addStatement(
                                    "val params = kotlinx.serialization.json.buildJsonObject { obj.forEach { (k, v) -> if (k != %S && k != %S) put(k, v) } }",
                                    "organizationId",
                                    "timestampMs"
                                )

                                // timestamp fallback to now
                                addStatement(
                                    "val ts = inputTimestamp?.%M?.content ?: System.currentTimeMillis().toString()",
                                    jsonPrimitive
                                )

                                // type = ACTIVITY_TYPE_<OP_ID in SNAKE>
                                val snake = rawId.substringAfter("_").toScreamingSnake()
                                // Resolve to either capped activity type, latest, or the input
                                val versioned =
                                    VersionedActivityTypes.map["ACTIVITY_TYPE_$snake"]?.first ?: latestVersions["ACTIVITY_TYPE_$snake"]?.fullName ?: "ACTIVITY_TYPE_$snake"
                                addStatement("val activityType = %S", versioned)

                                // compose final body
                                addStatement(
                                    "val bodyObj = kotlinx.serialization.json.buildJsonObject { " +
                                            "put(%S, params); " +                                        // parameters
                                            "inputOrgId?.let { put(%S, it) }; " +                          // organizationId (optional)
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
        .addFileComment(GENERATED_FILE_HEADER)
        .addAnnotation(
            AnnotationSpec.builder(Suppress::class)
                .useSiteTarget(AnnotationSpec.UseSiteTarget.FILE) // -> @file:Suppress(...)
                .addMember("%S", "unused")
                .addMember("%S", "UNUSED_PARAMETER")
                .addMember("%S", "UNUSED_VARIABLE")
                .addMember("%S", "RedundantVisibilityModifier")
                .addMember("%S", "MemberVisibilityCanBePrivate")
                .addMember("%S", "RedundantSuspendModifier")
                .build()
        )
        .addType(typeBuilder.build())
        .build()
        .writeTo(outRoot)

    println("✅ Generated $pkg.$className (combined)")
}