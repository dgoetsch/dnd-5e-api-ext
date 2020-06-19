package dandd.character.automation.generator

class ModelRegistrar(val pkg: String) {
    private val unnamedSchemas = mutableSetOf<ObjectSchema>()

    private val namedSchemas = mutableMapOf<String, ObjectSchema>()

    fun named() = namedSchemas.toMap()
    fun unnamed() = unnamedSchemas.toSet().filter {
        !namedSchemas.containsValue(it)
    }

    fun register(name: String?, objectSchema: ObjectSchema) {
        name?.let {
            namedSchemas.get(it)
                    ?.let { namedSchemas.put(name, objectSchema.computeOptionalFields(it)) }
                    ?: namedSchemas.put(name, objectSchema)
            unnamedSchemas.remove(objectSchema)
        }?: unnamedSchemas.add(objectSchema)
    }

    fun exportDictionary(): Map<String, ObjectSchema> =
            unnamed()
                    .map { it.fields.keys.joinToString("") { it.capitalize() }.capitalize() to it }
                    .toMap() + named()
                    .mapKeys { (key, _) ->
                        val key = key.split("_").map { it.capitalize() }.joinToString("")
                        if(key.matches("[0-9]+.*".toRegex())) {
                            "_$key"
                        } else {
                            key
                        }
                    }
                    .toMap()
}