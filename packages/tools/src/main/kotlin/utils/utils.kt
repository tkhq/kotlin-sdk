package utils

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Schema
import kotlinx.serialization.Serializable
import java.nio.file.Path

private val STRING = ClassName("kotlin", "String")
private val INT    = ClassName("kotlin", "Int")
private val LONG   = ClassName("kotlin", "Long")
private val DOUBLE = ClassName("kotlin", "Double")
private val BOOL   = ClassName("kotlin", "Boolean")
private val ANYN   = ClassName("kotlin", "Any")
private val UNIT   = ClassName("kotlin", "Unit")
private val LIST   = ClassName("kotlin.collections", "List")
private val MAP    = ClassName("kotlin.collections", "Map")

enum class OperationKind {
    Activity,          // default
    ActivityDecision,  // approveActivity / rejectActivity
    Noop,              // noop / anchor / ping
    Query,             // get*/list*/test* or HTTP GET
    Proxy              // proxy
}

/** Very small mapper: OpenAPI param schema → Kotlin type. */
fun kotlinTypeFromSchema(
    schema: Schema<*>?,
    modelPkg: String,
    modelPrefix: String?
): TypeName {
    if (schema == null) return STRING.copy(nullable = true)
    schema.`$ref`?.let { ref ->
        val name = ref.substringAfterLast("/")
        return ClassName(modelPkg, modelPrefix + name.capitalizeLeading())
    }
    val type = schema.type
    val format = schema.format
    return when (type) {
        "string" -> STRING
        "integer" -> if (format == "int64") LONG else INT
        "number" -> DOUBLE
        "boolean" -> BOOL
        "array" -> {
            val items = (schema as? ArraySchema)?.items
            LIST.parameterizedBy(kotlinTypeFromSchema(items, modelPkg, modelPrefix))
        }
        "object" -> MAP.parameterizedBy(STRING, ANYN.copy(nullable = true))
        else -> STRING
    }
}

fun String.capitalizeLeading(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

fun String.toScreamingSnake(): String =
    fold(StringBuilder()) { acc, c ->
        when {
            c.isUpperCase() -> {
                if (acc.isNotEmpty() && acc.last() != '_') acc.append('_')
                acc.append(c)
            }
            c.isLowerCase() -> acc.append(c.uppercaseChar())
            c.isDigit()     -> acc.append(c)
            else            -> acc.append('_')
        }
        acc
    }.toString()

fun classifyOperation(
    methodName: String,
    path: String? = null,
    prefix: String? = null
): OperationKind {
    val id = methodName.trim()
    val lower = id.lowercase()

    if (prefix?.lowercase() == "proxy") {
        return OperationKind.Proxy
    }

    // Activity decision endpoints
    if (id == "approveActivity" || id == "rejectActivity") {
        return OperationKind.ActivityDecision
    }

    // NOOPs – allow detection by name or path hint
    if (lower.startsWith("noop") || lower.startsWith("tnoop") || path?.contains("/noop", ignoreCase = true) == true) {
        return OperationKind.Noop
    }

    // Queries – GETs or method-name prefixes: get*/list*/test*
    if (methodName.startsWith("get") || methodName.startsWith("tget", ignoreCase = true) ||
        lower.startsWith("get") || lower.startsWith("tget") ||
        lower.startsWith("list") || lower.startsWith("tlist") ||
        lower.startsWith("test") || lower.startsWith("ttest")
    ) {
        return OperationKind.Query
    }

    // Default
    return OperationKind.Activity
}

private fun isValidIdentifier(name: String): Boolean =
    Regex("^[A-Za-z_][A-Za-z0-9_]*$").matches(name)

fun sanitizeFieldName(key: String): String {
    if (isValidIdentifier(key)) return key
    var s = key.replace(Regex("[^A-Za-z0-9_]"), "")
    if (s.isEmpty()) s = "field"
    if (!Regex("^[A-Za-z_]").containsMatchIn(s)) s = "_$s"
    return s
}

fun sanitizeEnumEntry(raw: String): String {
    var v = raw.trim().lowercase().replace(Regex("[^a-z0-9]+"), "_")
    v = v.replace(Regex("^_+|_+$"), "")
    if (v.isEmpty()) v = "value"
    if (Regex("^\\d").containsMatchIn(v)) v = "_$v"
    return v
}

fun sanitizeTypeName(raw: String): String {
    val base = raw.replace(".", "")
    return if (base.isEmpty()) "Type" else base.replaceFirstChar { it.uppercaseChar() }
}

fun ucFirst(s: String): String =
    if (s.isEmpty()) s else s[0].uppercaseChar() + s.substring(1)

fun TypeSpec.Builder.addSerializableAnnotations(): TypeSpec.Builder {
    addAnnotation(AnnotationSpec.builder(Serializable::class).build())
    return this
}

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