package utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Schema
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

/** Very small mapper: OpenAPI param schema → Kotlin type. */
fun kotlinTypeFromSchema(
    schema: Schema<*>?,
    modelPkg: String,
    modelPrefix: String?
): TypeName {
    if (schema == null) return STRING.copy(nullable = true)
    schema.`$ref`?.let { ref ->
        val name = ref.substringAfterLast("/")
        return ClassName(modelPkg, modelPrefix + name)
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