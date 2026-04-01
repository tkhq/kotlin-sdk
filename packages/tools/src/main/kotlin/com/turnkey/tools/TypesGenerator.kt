package com.turnkey.tools

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.turnkey.tools.utils.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.*
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories

/* Class names referenced by the oneOf serializer generator */
private val CN_JSON_CONTENT_POLYMORPHIC_SERIALIZER =
    ClassName("kotlinx.serialization.json", "JsonContentPolymorphicSerializer")
private val CN_DESERIALIZATION_STRATEGY =
    ClassName("kotlinx.serialization", "DeserializationStrategy")
private val CN_SERIALIZATION_EXCEPTION =
    ClassName("kotlinx.serialization", "SerializationException")
private val CN_JSON_ELEMENT = ClassName("kotlinx.serialization.json", "JsonElement")
private val CN_SERIALIZABLE = ClassName("kotlinx.serialization", "Serializable")

/** Entry point for generating Turnkey API types from OpenAPI specs */
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
    val typesFileName = arg("--types-file-name", "Models")

    specs.forEach { require(Files.exists(it.path)) { "Spec not found: ${it.path}" } }
    outRoot.createDirectories()

    // Parse all specs → OpenAPI 3
    val swaggerSpecs: List<Pair<SpecCfg, JsonObject>> = specs.map { it to readJson(it.path) }

    val fileBuilder = FileSpec.builder(pkg, typesFileName)
        .addFileComment(GENERATED_FILE_HEADER)
        .addImport("kotlinx.serialization", "Serializable", "SerialName")
        .addImport("kotlinx.serialization.json", "JsonElement")
        .addImport("kotlinx.serialization.json", "JsonContentPolymorphicSerializer", "jsonObject")
        .addImport("kotlinx.serialization", "DeserializationStrategy", "SerializationException")

    generateDefinitionsFromComponents(swaggerSpecs, fileBuilder, pkg)
    generateApiTypes(swaggerSpecs, fileBuilder, pkg)
    fileBuilder.addAnnotation(
        AnnotationSpec.builder(Suppress::class)
            .useSiteTarget(AnnotationSpec.UseSiteTarget.FILE) // -> @file:Suppress(...)
            .addMember("%S", "unused")
            .addMember("%S", "UNUSED_PARAMETER")
            .addMember("%S", "UNUSED_VARIABLE")
            .addMember("%S", "RedundantVisibilityModifier")
            .addMember("%S", "MemberVisibilityCanBePrivate")
            .addMember("%S", "RedundantSuspendModifier")
            .build()
    ).build().writeTo(outRoot.toFile())
}

/* -------------------------------------------------------------------------- */
/*                           TYPE MAPPING (JSON)                               */
/* -------------------------------------------------------------------------- */

private val TN_STRING: TypeName = STRING
private val TN_BOOLEAN: TypeName = BOOLEAN
private val TN_DOUBLE: TypeName = DOUBLE
private val TN_LONG: TypeName = LONG
private val TN_LIST = LIST
private val TN_MAP = MAP
private val TN_JSON_ELEMENT = ClassName("kotlinx.serialization.json", "JsonElement")

/** Map Swagger v2 JSON schema → KotlinPoet TypeName. */
private fun typeToKotlin(
    schema: JsonObject?,
    defs: Map<String, JsonObject>,
    pkg: String
): TypeName {
    // $ref wins
    schemaRefName(schema)?.let { return ClassName(pkg, sanitizeTypeName(it)) }

    // enums (we assume separate enum pass will generate actual enum classes by $ref;
    // inline enums stay as String)
    if (!jsonEnum(schema).isNullOrEmpty()) return TN_STRING

    when (jsonTypeOf(schema)?.lowercase()) {
        "string"  -> return TN_STRING
        "boolean" -> return TN_BOOLEAN
        "number"  -> return TN_DOUBLE
        "integer" -> return TN_LONG

        "array" -> {
            val inner = typeToKotlin(jsonItems(schema), defs, pkg)
            return TN_LIST.parameterizedBy(inner)
        }

        "object" -> {
            // If has properties → object type (we’ll generate a class elsewhere and reference via $ref),
            // but without $ref we map as Map<String, JsonElement> to be safe.
            val addl = jsonAdditionalProperties(schema)
            return when (addl) {
                is JsonObject -> {
                    val v = typeToKotlin(addl, defs, pkg)
                    TN_MAP.parameterizedBy(TN_STRING, v)
                }
                is JsonPrimitive -> if (addl.booleanOrNull == true)
                    TN_MAP.parameterizedBy(TN_STRING, TN_JSON_ELEMENT) else TN_MAP.parameterizedBy(TN_STRING, TN_JSON_ELEMENT)
                else -> {
                    // If explicit properties exist but no $ref, fallback to a generic map
                    if (jsonProperties(schema) != null) TN_MAP.parameterizedBy(TN_STRING, TN_JSON_ELEMENT)
                    else TN_MAP.parameterizedBy(TN_STRING, TN_JSON_ELEMENT)
                }
            }
        }
    }

    // No explicit type: if properties exist, map as generic object map
    if (jsonProperties(schema) != null) {
        return TN_MAP.parameterizedBy(TN_STRING, TN_JSON_ELEMENT)
    }

    return TN_JSON_ELEMENT
}

private fun emptyClassSpec(rawClassName: String): TypeSpec =
    TypeSpec.classBuilder(sanitizeTypeName(rawClassName))
        .addSerializableAnnotations()
        .primaryConstructor(FunSpec.constructorBuilder().build())
        .build()

/* -------------------------------------------------------------------------- */
/*                        ENUM & CLASS GENERATION (JSON)                       */
/* -------------------------------------------------------------------------- */

fun enumSpec(enumNameRaw: String, values: JsonArray): TypeSpec {
    val enumName = sanitizeTypeName(enumNameRaw)
    val b = TypeSpec.enumBuilder(enumName).addSerializableAnnotations()
    values.forEach { v ->
        val raw = v.jsonPrimitive.content
        val safe = sanitizeEnumEntry(raw).uppercase()
        b.addEnumConstant(
            safe,
            TypeSpec.anonymousClassBuilder()
                .addAnnotation(
                    AnnotationSpec.builder(SerialName::class)
                        .addMember("%S", raw)
                        .build()
                )
                .build()
        )
    }
    return b.build()
}

private data class FieldSpecInfo(
    val fieldName: String,
    val jsonKey: String,
    val typeName: TypeName,
    val nullable: Boolean,
    val description: String?
)

private fun classSpec(
    rawClassName: String,
    schema: JsonObject,
    defs: Map<String, JsonObject>,
    pkg: String
): TypeSpec {
    val className = sanitizeTypeName(rawClassName)
    val props = (schema["properties"] as? JsonObject) ?: buildJsonObject { }
    val required = (schema["required"] as? JsonArray)
        ?.mapNotNull { it.jsonPrimitive.contentOrNull }
        ?.toSet() ?: emptySet()

    val ctor = FunSpec.constructorBuilder()
    val tb = TypeSpec.classBuilder(className).addSerializableAnnotations()

    var fieldCount = 0
    props.toSortedMap().forEach { (jsonKey, pSchemaEl) ->
        fieldCount++
        val pSchema = pSchemaEl.jsonObject
        val isReq = jsonKey in required
        val tn = typeToKotlin(pSchema, defs, pkg).let { if (isReq) it else it.copy(nullable = true) }
        val sanitized = sanitizeFieldName(jsonKey)

        ctor.addParameter(
            ParameterSpec.builder(sanitized, tn)
                .apply { if (tn.isNullable) defaultValue("null") }
                .build()
        )

        tb.addProperty(
            PropertySpec.builder(sanitized, tn)
                .initializer(sanitized)
                .addAnnotation(
                    AnnotationSpec.builder(SerialName::class)
                        .addMember("%S", jsonKey)
                        .build()
                )
                .apply {
                    val desc = pSchema["description"]?.jsonPrimitive?.contentOrNull
                    if (!desc.isNullOrBlank()) addKdoc("%L\n", desc)
                }
                .build()
        )
    }

    // additionalProperties
    run {
        val ap = schema["additionalProperties"]
        when {
            ap is JsonObject -> {
                fieldCount++
                val valueT = typeToKotlin(ap, defs, pkg)
                val mapT = MAP.parameterizedBy(STRING, valueT).copy(nullable = true)
                ctor.addParameter(ParameterSpec.builder("additionalProperties", mapT).defaultValue("null").build())
                tb.addProperty(
                    PropertySpec.builder("additionalProperties", mapT)
                        .initializer("additionalProperties")
                        .addKdoc("Unrecognized properties captured here.\n")
                        .build()
                )
            }
            ap is JsonPrimitive && ap.booleanOrNull == true -> {
                fieldCount++
                val mapT = MAP.parameterizedBy(
                    STRING,
                    ClassName("kotlinx.serialization.json", "JsonElement")
                ).copy(nullable = true)
                ctor.addParameter(ParameterSpec.builder("additionalProperties", mapT).defaultValue("null").build())
                tb.addProperty(
                    PropertySpec.builder("additionalProperties", mapT)
                        .initializer("additionalProperties")
                        .addKdoc("Unrecognized properties captured here.\n")
                        .build()
                )
            }
            else -> { /* no-op */ }
        }
    }

    if (fieldCount > 0) tb.addModifiers(KModifier.DATA)

    schema["description"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }?.let {
        tb.addKdoc("%L\n", it)
    }

    return tb.primaryConstructor(ctor.build()).build()
}

/**
 * Generates a sealed interface + per-variant data classes + a polymorphic
 * serializer for Swagger definitions that contain oneOf-style fields.
 *
 * Returns a pair: (sealed interface TypeSpec, serializer object TypeSpec).
 */
private fun oneOfClassSpec(
    rawClassName: String,
    schema: JsonObject,
    defs: Map<String, JsonObject>,
    pkg: String,
    oneOfFieldNames: List<String>
): Pair<TypeSpec, TypeSpec> {
    val className = sanitizeTypeName(rawClassName)
    val classCN = ClassName(pkg, className)
    val serializerName = "${className}Serializer"
    val serializerCN = ClassName(pkg, serializerName)

    val props = (schema["properties"] as? JsonObject) ?: buildJsonObject { }
    val required = (schema["required"] as? JsonArray)
        ?.mapNotNull { it.jsonPrimitive.contentOrNull }
        ?.toSet() ?: emptySet()
    val oneOfSet = oneOfFieldNames.toSet()

    // Separate base fields from oneOf fields
    val baseFields = props.filterKeys { it !in oneOfSet }
    val oneOfFields = props.filterKeys { it in oneOfSet }

    // --- Sealed interface ---
    val sealedBuilder = TypeSpec.interfaceBuilder(className)
        .addModifiers(KModifier.SEALED)
        .addAnnotation(
            AnnotationSpec.builder(CN_SERIALIZABLE)
                .addMember("with = %T::class", serializerCN)
                .build()
        )

    schema["description"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }?.let {
        sealedBuilder.addKdoc("%L\n", it)
    }

    // --- Companion invoke factory so callers can use ClassName(...) syntax ---
    run {
        val invokeBuilder = FunSpec.builder("invoke")
            .addModifiers(KModifier.OPERATOR)
            .returns(classCN)

        // Add all base fields as parameters
        baseFields.toSortedMap().forEach { (jsonKey, sEl) ->
            val s = sEl.jsonObject
            val isReq = jsonKey in required
            val tn = typeToKotlin(s, defs, pkg).let { if (isReq) it else it.copy(nullable = true) }
            invokeBuilder.addParameter(
                ParameterSpec.builder(sanitizeFieldName(jsonKey), tn)
                    .apply { if (tn.isNullable) defaultValue("null") }
                    .build()
            )
        }

        // Add oneOf fields as nullable parameters (exactly one should be non-null)
        for (oneOfKey in oneOfFieldNames) {
            val oneOfSchema = oneOfFields[oneOfKey]?.jsonObject ?: continue
            val oneOfType = typeToKotlin(oneOfSchema, defs, pkg).copy(nullable = true)
            invokeBuilder.addParameter(
                ParameterSpec.builder(sanitizeFieldName(oneOfKey), oneOfType)
                    .defaultValue("null")
                    .build()
            )
        }

        // Build the when body that dispatches to the right variant
        invokeBuilder.addCode(buildCodeBlock {
            beginControlFlow("return when")
            for (oneOfKey in oneOfFieldNames) {
                val variantName = ucFirst(sanitizeFieldName(oneOfKey))
                val sanitized = sanitizeFieldName(oneOfKey)
                val baseArgs = baseFields.keys.sorted().joinToString(", ") { k ->
                    val s = sanitizeFieldName(k); "$s = $s"
                }
                val allArgs = "$sanitized = $sanitized" +
                    (if (baseArgs.isNotEmpty()) ", $baseArgs" else "")
                addStatement(
                    "$sanitized != null -> %T($allArgs)",
                    ClassName(pkg, className, variantName)
                )
            }
            addStatement(
                "else -> throw %T(%S)",
                ClassName("kotlin", "IllegalArgumentException"),
                "Exactly one of ${oneOfFieldNames.joinToString()} must be non-null"
            )
            endControlFlow()
        })

        sealedBuilder.addType(
            TypeSpec.companionObjectBuilder()
                .addFunction(invokeBuilder.build())
                .build()
        )
    }

    // --- One data-class variant per oneOf field ---
    for (oneOfKey in oneOfFieldNames) {
        val oneOfSchema = oneOfFields[oneOfKey]?.jsonObject ?: continue
        val variantName = ucFirst(sanitizeFieldName(oneOfKey))
        val variantBuilder = TypeSpec.classBuilder(variantName)
            .addModifiers(KModifier.DATA)
            .addAnnotation(AnnotationSpec.builder(CN_SERIALIZABLE).build())
            .addSuperinterface(classCN)

        val ctor = FunSpec.constructorBuilder()

        // oneOf field itself — always non-nullable (it defines the variant)
        val oneOfType = typeToKotlin(oneOfSchema, defs, pkg)
        ctor.addParameter(sanitizeFieldName(oneOfKey), oneOfType)
        variantBuilder.addProperty(
            PropertySpec.builder(sanitizeFieldName(oneOfKey), oneOfType)
                .initializer(sanitizeFieldName(oneOfKey))
                .addAnnotation(
                    AnnotationSpec.builder(SerialName::class)
                        .addMember("%S", oneOfKey)
                        .build()
                )
                .apply {
                    oneOfSchema["description"]?.jsonPrimitive?.contentOrNull
                        ?.takeIf { it.isNotBlank() }?.let { addKdoc("%L\n", it) }
                }
                .build()
        )

        // All base (shared) fields
        baseFields.toSortedMap().forEach { (jsonKey, sEl) ->
            val s = sEl.jsonObject
            val isReq = jsonKey in required
            val tn = typeToKotlin(s, defs, pkg).let { if (isReq) it else it.copy(nullable = true) }
            val sanitized = sanitizeFieldName(jsonKey)

            ctor.addParameter(
                ParameterSpec.builder(sanitized, tn)
                    .apply { if (tn.isNullable) defaultValue("null") }
                    .build()
            )
            variantBuilder.addProperty(
                PropertySpec.builder(sanitized, tn)
                    .initializer(sanitized)
                    .addAnnotation(
                        AnnotationSpec.builder(SerialName::class)
                            .addMember("%S", jsonKey)
                            .build()
                    )
                    .apply {
                        s["description"]?.jsonPrimitive?.contentOrNull
                            ?.takeIf { it.isNotBlank() }?.let { addKdoc("%L\n", it) }
                    }
                    .build()
            )
        }

        variantBuilder.primaryConstructor(ctor.build())
        sealedBuilder.addType(variantBuilder.build())
    }

    val sealedInterface = sealedBuilder.build()

    // --- JsonContentPolymorphicSerializer ---
    val selectFn = FunSpec.builder("selectDeserializer")
        .addModifiers(KModifier.OVERRIDE)
        .addParameter("element", CN_JSON_ELEMENT)
        .returns(CN_DESERIALIZATION_STRATEGY.parameterizedBy(classCN))
        .addCode(buildCodeBlock {
            beginControlFlow("return when")
            for (field in oneOfFieldNames) {
                val variantName = ucFirst(sanitizeFieldName(field))
                addStatement(
                    "%S in element.jsonObject -> %T.serializer()",
                    field, ClassName(pkg, className, variantName)
                )
            }
            addStatement(
                "else -> throw %T(%S)",
                CN_SERIALIZATION_EXCEPTION,
                "Unknown variant of $className: none of ${oneOfFieldNames.joinToString()} found"
            )
            endControlFlow()
        })
        .build()

    val serializer = TypeSpec.objectBuilder(serializerName)
        .superclass(CN_JSON_CONTENT_POLYMORPHIC_SERIALIZER.parameterizedBy(classCN))
        .addSuperclassConstructorParameter("%T::class", classCN)
        .addFunction(selectFn)
        .build()

    return sealedInterface to serializer
}


/* -------------------------------------------------------------------------- */
/*                         DEFINITIONS (ENUMS + CLASSES)                       */
/* -------------------------------------------------------------------------- */

fun generateDefinitionsFromComponents(
    apis: List<Pair<SpecCfg, JsonObject>>,
    fileBuilder: FileSpec.Builder,
    pkg: String
) {
    val seen = mutableSetOf<String>()

    // 1) Enums first
    apis.forEach { (_, swagger) ->
        val defs = (swagger["definitions"] as? JsonObject)?.mapValues { it.value.jsonObject } ?: emptyMap()
        defs.toSortedMap().forEach { (rawName, def) ->
            val enums = def["enum"] as? JsonArray
            val type = def["type"]?.jsonPrimitive?.contentOrNull
            if (rawName !in seen && enums != null && enums.isNotEmpty() &&
                (type == null || type.equals("string", ignoreCase = true))
            ) {
                fileBuilder.addType(enumSpec(rawName, enums))
                seen += rawName
            }
        }
    }

    // 2) Object classes with properties (including oneOf sealed interfaces)
    apis.forEach { (_, swagger) ->
        val defs = (swagger["definitions"] as? JsonObject)?.mapValues { it.value.jsonObject } ?: emptyMap()
        defs.toSortedMap().forEach { (rawName, def) ->
            if (rawName in seen) return@forEach
            val hasProps = (def["properties"] as? JsonObject)?.isNotEmpty() == true
            val isEnum = (def["enum"] as? JsonArray)?.isNotEmpty() == true
            if (hasProps && !isEnum) {
                val oneOfFields = OneOfFields.map[rawName]
                if (oneOfFields != null) {
                    val (sealedInterface, serializer) = oneOfClassSpec(rawName, def, defs, pkg, oneOfFields)
                    fileBuilder.addType(sealedInterface)
                    fileBuilder.addType(serializer)
                } else {
                    fileBuilder.addType(classSpec(rawName, def, defs, pkg))
                }
                seen += rawName
            }
        }
    }

    // 3) Empty object stubs (type: object, no properties/enum/addlProps)
    apis.forEach { (_, swagger) ->
        val defs = (swagger["definitions"] as? JsonObject)?.mapValues { it.value.jsonObject } ?: emptyMap()
        defs.toSortedMap().forEach { (rawName, def) ->
            if (rawName in seen) return@forEach
            val type = def["type"]?.jsonPrimitive?.contentOrNull
            val hasProps = (def["properties"] as? JsonObject)?.isNotEmpty() == true
            val hasEnum = (def["enum"] as? JsonArray)?.isNotEmpty() == true
            val hasAddl = def["additionalProperties"] != null
            if (type == "object" && !hasProps && !hasEnum && !hasAddl) {
                fileBuilder.addType(emptyClassSpec(rawName))
                seen += rawName
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                             API TYPES (JSON)                                */
/* -------------------------------------------------------------------------- */

fun generateApiTypes(
    apis: List<Pair<SpecCfg, JsonObject>>,
    fileBuilder: FileSpec.Builder,
    pkg: String,
) {
    fun isAllOptionalFor(methodName: String) =
        methodName in setOf(
            "getActivities","getApiKeys","getOrganization","getPolicies","getPrivateKeys",
            "getSubOrgIds","getUsers","getWallets","getWhoami","listPrivateKeys","listUserTags"
        )
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

    val tStampClass = ClassName(pkg, "TStamp")
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

    apis.forEach { (cfg, swagger) ->
        val specPrefix = cfg.prefix
        val defs = swagger.definitions()
        val pths = swagger.paths()
        val tagName = (swagger.tags()?.firstOrNull() as? JsonObject)
            ?.get("name")?.jsonPrimitive?.content
        val namespace = tagName
        val latestVersions = extractLatestVersions(defs)

        pths.forEach { (_, methodsObj) ->
            val post = methodsObj["post"] as? JsonObject ?: return@forEach
            val operationId = post["operationId"]?.jsonPrimitive?.content ?: return@forEach

            val opNameNoNs = if (namespace != null && operationId.startsWith("${namespace}_")) {
                operationId.replaceFirst("${namespace}_", "T")
            } else {
                if (operationId.startsWith("T")) operationId else "T$operationId"
            }

            val methodName = opNameNoNs.replaceFirstChar { it.lowercase() }
            val kind = classifyOperation(methodName, null, specPrefix)

            // ----- RESPONSE -----
            val responses = post["responses"] as? JsonObject
            val ok = responses?.get("200") as? JsonObject
            val schema = ok?.get("schema") as? JsonObject
            val responseRefName = schemaRefName(schema)

            val apiTypeName = "${specPrefix}${ucFirst(opNameNoNs)}Response"
            val apiBodyTypeName = "${specPrefix}${ucFirst(opNameNoNs)}Body"
            val apiInputTypeName = "${specPrefix}${ucFirst(opNameNoNs)}Input"

            when (kind) {
                OperationKind.Activity -> {
                    // Find result type from request parameters.type enum mapping
                    val params = (post["parameters"] as? JsonArray)?.toList().orEmpty()
                    var resultTypeName: String? = null

                    for (p in params) {
                        val pm = p.jsonObject
                        if (pm["in"]?.jsonPrimitive?.content == "body") {
                            val sch = pm["schema"] as? JsonObject ?: continue
                            val rqRefName = schemaRefName(sch) ?: continue
                            val reqDef = defs[rqRefName] ?: continue
                            val reqProps = jsonProperties(reqDef) ?: continue
                            val typeEnum = reqProps["type"]?.jsonObject
                                ?.get("enum") as? JsonArray ?: continue
                            if (typeEnum.isEmpty()) continue

                            val raw = typeEnum.first().jsonPrimitive.contentOrNull ?: ""
                            val activityTypeKey = raw.replace(Regex("_V\\d+$", RegexOption.IGNORE_CASE), "")

                            val baseActivity = rqRefName
                                .replace(Regex("^v\\d+"), "")
                                .replace(Regex("Request(V\\d+)?$"), "")

                            val mapped = activityTypeKey.let { VersionedActivityTypes.map[it] }

                            val candidate = mapped?.let { m ->
                                defs.keys.firstOrNull { k ->
                                    k == m.third
                                }
                            }

                            // take either the found candidate or the latest intent version
                            resultTypeName = candidate?.takeIf { it.isNotEmpty() } ?: latestVersions["${baseActivity}Result"]?.fullName
                        }
                    }

                    val activityCN = ClassName(pkg, sanitizeTypeName("v1Activity"))
                    val resp = TypeSpec.classBuilder(apiTypeName)
                        .addSerializableAnnotations()
                        .primaryConstructor(
                            FunSpec.constructorBuilder()
                                .addParameter("activity", activityCN)
                                .apply {
                                    if (resultTypeName != null && defs.containsKey(resultTypeName)) {
                                        addParameter(
                                            ParameterSpec.builder(
                                                "result",
                                                ClassName(pkg, sanitizeTypeName(resultTypeName))
                                            ).build()
                                        )
                                    }
                                }
                                .build()
                        )
                        .addProperty(
                            PropertySpec.builder("activity", activityCN)
                                .initializer("activity")
                                .addAnnotation(AnnotationSpec.builder(SerialName::class).addMember("%S", "activity").build())
                                .build()
                        )
                        .apply {
                            if (resultTypeName != null && defs.containsKey(resultTypeName)) {
                                addProperty(
                                    PropertySpec.builder(
                                        "result",
                                        ClassName(pkg, sanitizeTypeName(resultTypeName))
                                    )
                                        .initializer("result")
                                        .addAnnotation(AnnotationSpec.builder(SerialName::class).addMember("%S", "result").build())
                                        .build()
                                )
                            }
                        }
                        .build()

                    fileBuilder.addType(resp)
                }

                OperationKind.Query, OperationKind.Noop, OperationKind.Proxy -> {
                    val target = responseRefName?.let { defs[it] }
                    if (target != null && jsonProperties(target)?.isNotEmpty() == true) {
                        // Inline a fake object def for classSpec
                        val fake = buildJsonObject {
                            put("type", JsonPrimitive("object"))
                            put("properties", jsonProperties(target)!!)
                            target["required"]?.let { put("required", it) }
                        }
                        fileBuilder.addType(classSpec(apiTypeName, fake, defs, pkg))
                    } else {
                        fileBuilder.addType(
                            TypeSpec.classBuilder(apiTypeName)
                                .addSerializableAnnotations()
                                .primaryConstructor(FunSpec.constructorBuilder().build())
                                .build()
                        )
                    }
                }

                OperationKind.ActivityDecision -> {
                    val target = responseRefName?.let { defs[it] }
                    if (target != null && jsonProperties(target)?.isNotEmpty() == true) {
                        val fake = buildJsonObject {
                            put("type", JsonPrimitive("object"))
                            put("properties", jsonProperties(target)!!)
                            target["required"]?.let { put("required", it) }
                        }
                        fileBuilder.addType(classSpec(apiTypeName, fake, defs, pkg))
                    }
                }
            }

            // ----- REQUEST BODY (...Body + ...Input) -----
            val requestTypeDef: JsonObject? = run {
                val params = (post["parameters"] as? JsonArray)?.toList().orEmpty()
                params.firstNotNullOfOrNull { p ->
                    val pm = p.jsonObject
                    if (pm["in"]?.jsonPrimitive?.content == "body") {
                        val r = (pm["schema"] as? JsonObject)?.get("\$ref")?.jsonPrimitive?.content ?: return@firstNotNullOfOrNull null
                        defs[refToName(r)]
                    } else null
                }
            }

            if (requestTypeDef == null) {
                if (kind == OperationKind.Noop) {
                    val body = TypeSpec.classBuilder(apiBodyTypeName)
                        .addSerializableAnnotations()
                        .primaryConstructor(FunSpec.constructorBuilder().build())
                        .build()
                    fileBuilder.addType(body)

                    fileBuilder.addType(
                        TypeSpec.classBuilder(apiInputTypeName)
                            .addSerializableAnnotations()
                            .primaryConstructor(
                                FunSpec.constructorBuilder()
                                    .addParameter("body", ClassName(pkg, apiBodyTypeName))
                                    .build()
                            )
                            .addProperty(
                                PropertySpec.builder("body", ClassName(pkg, apiBodyTypeName))
                                    .initializer("body")
                                    .addAnnotation(AnnotationSpec.builder(SerialName::class).addMember("%S", "body").build())
                                    .build()
                            )
                            .build()
                    )
                }
                return@forEach
            }

            val bodyFields = mutableListOf<FieldSpecInfo>()
            fun addField(name: String, jsonKey: String, tn: TypeName, required: Boolean) {
                bodyFields += FieldSpecInfo(
                    fieldName = name,
                    jsonKey = jsonKey,
                    typeName = if (required) tn else tn.copy(nullable = true),
                    nullable = !required,
                    description = null
                )
            }

            when (kind) {
                OperationKind.Activity, OperationKind.ActivityDecision -> {
                    addField("timestampMs", "timestampMs", TN_STRING.copy(nullable = true), required = false)
                    addField("organizationId", "organizationId", TN_STRING.copy(nullable = false), required = true)

                    val reqProps = jsonProperties(requestTypeDef)
                    val parametersRef = schemaRefName(reqProps?.get("parameters") as? JsonObject)

                    // get raw activity type & parse out the versioning
                    val raw = reqProps?.get("type")?.jsonObject?.get("enum")?.jsonArray?.get(0)?.jsonPrimitive?.contentOrNull ?: ""
                    val activityType = raw.replace(Regex("_V\\d+$", RegexOption.IGNORE_CASE), "")

                    // strip the intent & versioning to get the base activity
                    val baseActivity = parametersRef
                        ?.replace(Regex("^v\\d+"), "")
                        ?.replace(Regex("Intent(V\\d+)?$"), "")

                    // find the proper intent version according to our VersionedActivityTypes map
                    val mapped = activityType.let { VersionedActivityTypes.map[it] }

                    // search for intents matching the above version
                    val candidate = mapped?.let { m ->
                        defs.keys.firstOrNull { k ->
                            k == m.second
                        }
                    }

                    // use the candidate (from versioned map) found or just fall back to the request's parameter intent (latest)
                    val intentDef = candidate?.let { defs[it] } ?: parametersRef?.let { defs[it] }
                    val iprops = jsonProperties(intentDef) ?: buildJsonObject { }
                    val reqd = jsonRequired(intentDef)
                    val allOpt = isAllOptionalFor(methodName)

                    iprops.forEach { (k, sEl) ->
                        val s = sEl.jsonObject
                        val tn = typeToKotlin(s, defs, pkg)
                        val isReq = if (allOpt) false else reqd.contains(k)
                        addField(sanitizeFieldName(k), k, tn, isReq)
                    }
                }

                OperationKind.Query, OperationKind.Noop -> {
                    addField("organizationId", "organizationId", TN_STRING.copy(nullable = true), required = false)
                    val props = jsonProperties(requestTypeDef) ?: buildJsonObject { }
                    val reqd = jsonRequired(requestTypeDef)
                    val allOpt = isAllOptionalFor(methodName)

                    props.forEach { (k, sEl) ->
                        if (k == "organizationId") return@forEach
                        val s = sEl.jsonObject
                        val tn = typeToKotlin(s, defs, pkg)
                        val isReq = if (allOpt) false else reqd.contains(k)
                        addField(sanitizeFieldName(k), k, tn, isReq)
                    }
                }

                OperationKind.Proxy -> {
                    val props = jsonProperties(requestTypeDef) ?: buildJsonObject { }
                    val reqd = jsonRequired(requestTypeDef)
                    val allOpt = isAllOptionalFor(methodName)

                    props.forEach { (k, sEl) ->
                        val s = sEl.jsonObject
                        val tn = typeToKotlin(s, defs, pkg)
                        val isReq = if (allOpt) false else reqd.contains(k)
                        addField(sanitizeFieldName(k), k, tn, isReq)
                    }
                }
            }

            // Body data class
            run {
                val ctor = FunSpec.constructorBuilder().apply {
                    bodyFields.forEach { f ->
                        addParameter(
                            ParameterSpec.builder(f.fieldName, f.typeName)
                                .apply {
                                    if (f.nullable) defaultValue("null")
                                }
                                .build()
                        )
                    }
                }.build()

                val t = TypeSpec.classBuilder(apiBodyTypeName)
                    .addSerializableAnnotations()
                    .primaryConstructor(ctor)
                    .apply {
                        bodyFields.forEach { f ->
                            addProperty(
                                PropertySpec.builder(f.fieldName, f.typeName)
                                    .initializer(f.fieldName)
                                    .addAnnotation(
                                        AnnotationSpec.builder(SerialName::class)
                                            .addMember("%S", f.jsonKey)
                                            .build()
                                    )
                                    .build()
                            )
                        }
                    }
                    .build()

                fileBuilder.addType(t)
            }

            // Input wrapper
            run {
                val t = TypeSpec.classBuilder(apiInputTypeName)
                    .addSerializableAnnotations()
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter("body", ClassName(pkg, apiBodyTypeName))
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("body", ClassName(pkg, apiBodyTypeName))
                            .initializer("body")
                            .addAnnotation(AnnotationSpec.builder(SerialName::class).addMember("%S", "body").build())
                            .build()
                    )
                    .build()

                fileBuilder.addType(t)
            }
        }
    }
}
