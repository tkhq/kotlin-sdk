package generators

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
import io.swagger.v3.oas.models.OpenAPI
import utils.OperationKind
import utils.SpecCfg
import utils.VersionedActivityTypes
import utils.capitalizeLeading
import utils.classifyOperation
import utils.toScreamingSnake
import java.nio.file.Path

fun generateClientFile(
    apis: List<Pair<SpecCfg, OpenAPI>>,
    outRoot: Path,
    pkg: String,
    modelsPkg: String,
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
                    ClassName(modelsPkg, typeName)
                }

                // Response type (or Unit)
                val respType: TypeName = respSchemaRef?.let { ref ->
                    val schemaName = opId.substringAfter("_")
                    val typeName = (opPrefix.ifBlank { "" }) + "T" + schemaName + "Response"
                    ClassName(modelsPkg, typeName)
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
                                val versioned =
                                    VersionedActivityTypes.resolve("ACTIVITY_TYPE_$snake")
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
                        addStatement("val resp = call.%M()", awaitM)
                        beginControlFlow("resp.use {")
                        beginControlFlow("if (!it.isSuccessful)")
                        addStatement(
                            "val errBody = %M(%T.IO) { kotlin.runCatching { it.body.string() }.getOrNull() }",
                            withContextM, dispatchersCls
                        )
                        addStatement("throw RuntimeException(%P + it.code)", "HTTP error from $path: ")
                        endControlFlow()
                        addStatement(
                            "val text = %M(%T.IO) { it.body.string() }",
                            withContextM, dispatchersCls
                        )
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
                    val tStampCls = ClassName(modelsPkg, "TStamp")
                    val tSignedReqCls = ClassName(modelsPkg, "TSignedRequest")
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
                                val versioned =
                                    VersionedActivityTypes.resolve("ACTIVITY_TYPE_$snake")
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