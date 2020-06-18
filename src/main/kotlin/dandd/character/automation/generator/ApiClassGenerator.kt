package dandd.character.automation.generator

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.lang.RuntimeException


fun main() {
    val name = "Sorcerer"
    val json = File("classes/$name.json").readText()
    val pkg = "dandd.character.automation"
    val classInterface = JsonClassInterface(pkg, name, json, ClassRegistrar(pkg))
    val model = classInterface.modelTree()

    println("NAMED")
    println(classInterface.classRegistrar.named().entries.joinToString(",\n"))
    println("======")
    println("UNNAMED")
    println(classInterface.classRegistrar.unnamed().joinToString(",\n"))
    println("======")

}

data class JsonClassInterface(
        val pkg: String,
        val name: String,
        val json: String,
        val classRegistrar: ClassRegistrar) {
    fun modelTree() = ModelTree(name, pkg, keyValues, classRegistrar)
    fun generate(): String =
        """
            package $pkg
            
            ${imports.joinToString("\n")}
            
            $classDeclaration
            $indent${fields.joinToString(",\n$indent")}
            )
        """.trimIndent()
    private val objectMapper = jacksonObjectMapper()
    private val classDeclaration = "data class $name("
    private val indent = " ".repeat(classDeclaration.length)
    private val keyValues: Map<String, Any> =
            objectMapper
                    .readValue(json, Map::class.java)
                    .mapNotNull { entry -> entry.key?.let { key -> entry.value?. let { value -> when(key) {
                        is String -> key to value
                        else -> null
                    } } }}
                    .toMap()

    private val fields = keyValues.map { (key, value) -> "val $key: ${findClass(key, value)}" }
    private val imports = keyValues.map { (_, value) -> "import ${value.javaClass.name}" }.distinct().sorted()

    private val objectChildren = keyValues
            .mapNotNull { (key, value) ->
                when(value) {
                    is Map<*, *> -> key to value
                    else -> null
                }
            }
    private val listChildren = keyValues
            .mapNotNull { (key, value) ->
                when(value) {
                    is List<*> -> key to value
                    else -> null
                }
            }




    private fun processMapChild(child: Map<*, *>) {
        val childJson = objectMapper.writeValueAsString(child)
    }
    /**->
     * Todo lookup table
     */
    private fun findClass(key: String, value: Any): String = value::class.java.name
}

class ClassRegistrar(val pkg: String) {
    private val unnamedSchemas = mutableSetOf<ObjectSchema>()

    private val namedSchemas = mutableMapOf<String, ObjectSchema>()

    fun named() = namedSchemas.toMap()
    fun unnamed() = unnamedSchemas.toSet().filter {
        !namedSchemas.containsValue(it)
    }

    fun register(name: String?, objectSchema: ObjectSchema) {
        name?.let {
            namedSchemas.put(name, objectSchema)
            unnamedSchemas.remove(objectSchema)
        }?: unnamedSchemas.add(objectSchema)
    }
    fun findClass(key: String, value: Any): ClassRef = className(key, value)

    private fun className(key: String, value: Any): ClassRef =
        when(value) {
            is Map<*, *> -> ClassRef(pkg, key.split("_").joinToString(separator = "") { it.capitalize() })
            else -> ClassRef(value.javaClass.packageName, value.javaClass.simpleName)
        }
}

data class ClassRef(val pkg: String, val name: String)


data class ModelTree(val name: String, val pkg: String, val keyValues: Map<*, *>, val classRegistrar: ClassRegistrar) {
    val schemas = keyValues.objectSchemas(name)

    private fun Map<*, *>.objectSchemas(name: String? = null):  Set<ObjectSchema> {
         val thisSchema = ObjectSchema(this.mapNotNull { entry -> entry.key?.let { key -> entry.value?.let { value -> when(key) {
            is String ->
                key to classRegistrar.findClass(key, value)
            else -> null
        } } } }.toMap())

        classRegistrar.register(name, thisSchema)

        val childSchemas: Set<ObjectSchema> =  this
                .mapNotNull { entry -> entry.key?.let { key -> entry.value?.let { value -> when(key) {
                    is String ->
                        when(value) {
                            is Map<*, *> -> value.objectSchemas(key)
                            is List<*> -> value.objectSchemas()
                            else -> emptySet()
                        }
                        else -> null
                } } } }
                .flatten()
                .toSet()

        return childSchemas + thisSchema
    }

    private fun List<*>.objectSchemas(): Set<ObjectSchema> = this
            .mapNotNull {
                when(it) {
                    is Map<*, *> -> it.objectSchemas()
                    is List<*> -> it.objectSchemas()
                    else -> null
                }
            }
            .flatten()
            .toSet()

}


data class ObjectSchema(val fields: Map<String, ClassRef>)