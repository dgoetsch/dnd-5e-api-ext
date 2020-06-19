package dandd.character.automation.generator

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import dandd.character.automation.Result

class Resources(
        val objectMapper: ObjectMapper
) {

    val readIndexField: suspend (String) -> Result<String> =
            { json: String ->
                Either.catch {
                    val index = json.toMap(objectMapper).get("index")
                    when (index) {
                        null -> throw java.lang.RuntimeException(" No INdex for $json")
                        else -> index.toString()
                    }
                }
            }

    val readNameField: suspend (String) -> Result<String> = {
        Either.catch {
            val name = it.toMap(objectMapper).get("name")
            when (name) {
                null -> throw RuntimeException("name not found in $it")
                else -> name.toString()
            }
        }
    }
    fun of(body: Resources.() -> List<ResourceConfig>): List<ResourceConfig> = body()

    fun resource(
            name: String,
            className: String,
            readName: suspend (String) -> Result<String> = readNameField,
            readIndex: suspend (String) -> Result<String> = readIndexField
    ) = ResourceConfig(name, className, readName, readIndex)


    fun getClassName(
            name: String
    ): suspend (String) -> Result<String> =
            { json: String ->
                Either.catch {
                    val characterClassRef = json.toMap(objectMapper).get("class")
                    val characterClassName = when (characterClassRef) {
                        is Map<*, *> -> characterClassRef.get("name")
                        else -> throw java.lang.RuntimeException("$name did not have a name")
                    }
                    when(characterClassName) {
                        null -> throw java.lang.RuntimeException("$name $characterClassName was not a string")
                        else -> "${characterClassName}"
                    }
                }
            }


}