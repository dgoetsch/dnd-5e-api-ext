package dandd.character.automation.source

import arrow.core.Either
import arrow.core.extensions.either.monad.flatten
import dandd.character.automation.Result
import org.json.JSONObject

internal data class HttpResourceLoader<T>(val urlBase: String,
                                 val resourceType: String,
                                 val readingMapper: suspend (String) -> Result<T>): ResourceLoader<T> {
    companion object {
        data class NotFound(val resourceType: String, val name: String): Throwable("Could not find $resourceType with name: $name")
        data class ToManyResources(val resourceType: String, val name: String): Throwable("More than one $resourceType matched $name")
        data class BadResponseFormat(val resourceType: String, val name: String, val value: Any): Throwable("$resourceType $name was the wrong format, was ${value}")
    }

    override suspend fun loadResource(name: String): Result<T> = Either.catch {
        val results = khttp.get("$urlBase/api/$resourceType?name=${name.replace("\\s+".toRegex(), "+")}")
                .jsonObject
                .getJSONArray("results")
                .toList()


        val resultObject = when (results) {
            emptyList<Any?>() -> throw NotFound(resourceType, name)
            results.take(1) -> results.first()
            else -> {
                println("multiple spells matched $name")
                results
                        .filter {
                            when (it) {
                                is JSONObject -> it.getString("name") == name
                                else -> false
                            }
                        }
                        .firstOrNull()?: ToManyResources(resourceType, name)
            }

        }

        val url = when (resultObject) {
            is JSONObject -> resultObject.getString("url")
            else -> throw BadResponseFormat(resourceType, name, resultObject)
        }

        val getResponse = khttp.get("$urlBase$url")

        val content = String(getResponse.content)

        readingMapper(content)
    }.flatten()
}