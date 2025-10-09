package utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Schema
import kotlin.collections.forEach

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
    Query              // get*/list*/test* or HTTP GET
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
    path: String? = null
): OperationKind {
    val id = methodName.trim()
    val lower = id.lowercase()

    // Activity decision endpoints
    if (id == "approveActivity" || id == "rejectActivity") {
        return OperationKind.ActivityDecision
    }

    // NOOPs – allow detection by name or path hint
    if (lower.startsWith("noop") || path?.contains("/noop", ignoreCase = true) == true) {
        return OperationKind.Noop
    }

    // Queries – GETs or method-name prefixes: get*/list*/test*
    if (methodName.startsWith("get", ignoreCase = true) ||
        lower.startsWith("get") ||
        lower.startsWith("list") ||
        lower.startsWith("test")
    ) {
        return OperationKind.Query
    }

    // Default
    return OperationKind.Activity
}