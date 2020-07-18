package dandd.character.automation.generator

import java.io.File

class KotlinClassWriter(val pkg: String, val dictionary: Map<String, ObjectSchema>) {
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
                } }

        val classDeclaration = "data class $name("
        val indent = "    "
        return """
package $pkg

${imports.joinToString("\n")}

$classDeclaration
$indent${fields.map { (name, schema) -> "val ${escapeFieldName(name)}: ${schema.writeType()}" }.joinToString(",\n$indent")}
)
"""
    }

    private fun Schema.writeType(): String =
            when(this) {
                is ConcreteSchema -> transformTypes[this]?:this.name()
                is ListSchema -> "List<${this.schema?.writeType()?:"Any"}>"
                is ObjectSchema -> dictionary.entries.find { it.value == this }?.key ?: "Map<String, Any>"
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
                "`$name`"
            } else {
                "$name"
            }


}