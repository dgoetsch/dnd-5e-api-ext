package dandd.character.automation.source

import arrow.core.Either
import arrow.core.extensions.either.monad.flatten
import arrow.core.flatMap
import dandd.character.automation.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import java.lang.RuntimeException
suspend fun <A, B, C> Either<A, B>.suspendFlatMap(f: suspend (B) -> Either<A, C>) =
        when(this) {
            is Either.Left -> Either.left(this.a)
            is Either.Right -> f(this.b)
        }
fun <A, B> Either<A, List<Either<A, B>>>.lift() =
    when(this) {
        is Either.Left -> listOf(Either.left(this.a))
        is Either.Right -> this.b
    }

internal data class HttpResourceLoader<T>(val urlBase: String,
                                 val resourceType: String,
                                 val readingMapper: suspend (String) -> Result<T>): ResourceLoader<T> {
    companion object {
        data class NotFound(val resourceType: String, val name: String): Throwable("Could not find $resourceType with name: $name")
        data class ToManyResources(val resourceType: String, val name: String): Throwable("More than one $resourceType matched $name")
        data class BadResponseFormat(val resourceType: String, val name: String, val value: Any): Throwable("$resourceType $name was the wrong format, was ${value}")
    }

    override suspend fun loadAll(excludeIds: Set<String>): List<Result<T>> {
        val result = Either.catch {
            khttp.get("$urlBase/api/$resourceType")
                    .jsonObject
                    .getJSONArray("results")
                    .toList()
        }.suspendFlatMap { queryResults -> Either.catch { coroutineScope {
            queryResults.map { async {
                Either.catch {
                    when (it) {
                        is JSONObject -> {
                            val id = it.get("index")
                            if(excludeIds.contains(id)) {
                                null
                            } else {
                                println("$id downloading")
                                val url = it.getString("url")
                                val response = khttp.get("$urlBase$url")
                                println("$id done")
                                String(response.content)
                            }
                        }
                        else -> throw RuntimeException("")
                    }
                }.suspendFlatMap {
                    when(it) {
                        null -> Either.right(it)
                        else -> readingMapper(it)
                    }
                }
            } }.awaitAll()
        } } }

        return when(result) {
            is Either.Left -> listOf(Either.left(result.a))
            is Either.Right -> result.b.mapNotNull {
                when(it) {
                    is Either.Right -> it.b?.let { b -> Either.right(b) }
                    is Either.Left -> Either.left(it.a)
                }
            }
        }
    }
    override suspend fun loadAll(): List<Result<T>> {
        return loadAll(emptySet())
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


