package dandd.character.automation.generator

import java.io.File

class KotlinClassWriter(val resourceName: String, val mainClassName: String, val pkg: String, val dictionary: Map<String, ObjectSchema>) {
    private val transformTypes = mapOf(ConcreteSchema(java.lang.Integer::class.java) to "Int")

    fun writeAll(directory: String) {
        val targetDirectory = "$directory/${pkg.split(".").joinToString("/")}"
        File(targetDirectory).mkdirs()

        dictionary.forEach { (name, schema) ->
            File("$targetDirectory/$name.kt").writeText(schema.write(name))
        }
    }

    private fun ObjectSchema.write(name: String): String {
        val ignoreImports = setOf("Int", "String", "Map", "List", "Long", "Integer", "Boolean", "Double")
        val imports = fields
                .filter {(name, schema) ->
                    when(schema) {
                        is ConcreteSchema -> !ignoreImports.contains(schema.name())
                        else -> false
                    }
                }
                .mapNotNull { (name, schema) -> when(schema) {
                    is ConcreteSchema -> "import ${schema.pkg()}.${schema.name()}"
                    else -> null
                } } + listOf(
                        "import io.ktor.client.HttpClient",
                        "import web.api.ApiCient",
                        "import web.api.ClientParseError",
                        "import web.core.Either",
                        "import web.core.bindRight",
                        "import web.core.mapLeft",
                        "import web.parse.*",
                        "import kotlin.js.Json"
        )

        val classDeclaration = "data class $name("
        val indent = "    "
        return """
package $pkg

${imports.joinToString("\n")}

$classDeclaration
$indent${fields.map { (name, schema) -> "val ${escapeFieldName(name)}: ${schema.writeType()}" }.joinToString(",\n$indent")}
) {
    companion object {
        val parseResponseBody = { jsonString: String -> Either
            .catching { JSON.parse<Json>(jsonString) }
            .mapLeft { JsonParse(it) }
            .bindRight { from(it) }
            .mapLeft { ClientParseError(it) }
        }
        
        fun from(json: Json?): Either<ParseError, $name> =
            json.parse {
                obj {
                    $name(
${writeParserFields().joinToString(",\n")}
                    )
                }
            }
        ${clientMethod(name)}
    }
}
"""
    }

    fun clientMethod(name: String): String {
        if (name == mainClassName) {
            return """
        fun client(httpClient: HttpClient): ApiCient<$name> =
            ApiCient(httpClient, "$resourceName", parseResponseBody)
"""
        } else {
            return  ""
        }
    }
    private fun ObjectSchema.writeParserFields(): List<String> {
        val writer = KotlinJsonParserWriter(dictionary, 6)
        return fields.map { (fieldName, value) ->
            writer.generateParser(value, fieldName)
        }
    }

    private fun Schema.writeType(): String =
            when(this) {
                is ConcreteSchema -> transformTypes[this]?:this.name()
                is ListSchema -> "List<${this.schema?.writeType()?:"Any"}>"
                is ObjectSchema -> dictionary.entries.find { this.isInstanceOf(it.value) }?.key ?: "Map<String, Any>"
                is OptionalSchema -> "${this.schema.writeType()}?"
            }

    private val replacementTypeName = mapOf(
            ConcreteSchema(Integer::class.java) to "Int"
    )
    private fun replaceSchema(concreteSchema: ConcreteSchema) {
        replacementTypeName.get(concreteSchema)?:concreteSchema.name()
    }
    private val keywords = setOf("if", "else", "class", "fun", "private", "internal", "public", "sealed", "when",
            "this", "protected", "for", "while", "return", "as", "break", "continue", "do", "false", "true", "in",
            "is", "null", "object", "super", "throw", "try", "typealias", "typeof", "val", "var")

    private fun escapeFieldName(name: String): String =
            if(keywords.contains(name)) {
                "`$name`"
            } else if(name.matches("[0-9]+.*".toRegex())) {
                "_$name"
            } else {
                "$name"
            }


}

