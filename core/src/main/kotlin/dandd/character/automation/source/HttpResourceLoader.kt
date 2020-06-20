package dandd.character.automation.source

import arrow.core.Either
import arrow.core.extensions.either.monad.flatten
import dandd.character.automation.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
                        is JSONObject -> it.get("index").toString()
                        else -> throw RuntimeException("$it was not a json object")
                    }
                }.suspendFlatMap{
                    loadResource(it)
                }.mapLeft {
                    println("encountered an error while loading resource ${resourceType}: $queryResults")
                    it
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

    override suspend fun loadResource(id: String): Result<T> = Either.catch {
        println("$id downloading")
        val getResponse =  khttp.get("$urlBase/api/$resourceType/${id.replace("\\s+".toRegex(), "+")}")

        val content = String(getResponse.content)
        println("$id completed downloading")
        readingMapper(content)
    }.flatten()
}


