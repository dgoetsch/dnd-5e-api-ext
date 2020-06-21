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
suspend fun <A, B, C> Either<A, B?>.suspendFlatMapNotNull(f: suspend (B) -> Either<A, C>) =
        when(this) {
            is Either.Left -> Either.left(this.a)
            is Either.Right -> {
                val result = b
                when (result) {
                    null -> Either.Right(null)
                    else -> f(result)
                }
            }
        }
fun <A, B> Either<A, List<Either<A, B>>>.lift() =
    when(this) {
        is Either.Left -> listOf(Either.left(this.a))
        is Either.Right -> this.b
    }

fun <A, B> List<Either<A, B?>>.filterRightNotNull(): List<Either<A, B>> =
        mapNotNull { when(it) {
            is Either.Right -> it.b?.let { Either.right(it) }
            is Either.Left -> it
        } }

internal data class HttpResourceLoader<T>(val urlBase: String,
                                 val resourceType: String,
                                 val readingMapper: suspend (String) -> Result<T>): ResourceLoader<T> {
    override suspend fun loadAll(excludeIds: Set<String>): List<Result<T>> =
        Either
                .catch {
                    khttp.get("$urlBase/api/$resourceType")
                            .jsonObject
                            .getJSONArray("results")
                            .toList()
                }
                .suspendFlatMap { queryResults -> Either.catch { coroutineScope {
                    queryResults
                            .map { resultEntry -> async {
                                fetchOrSkip(resultEntry, excludeIds)
                            } }
                            .awaitAll()
                            .filterRightNotNull()
                } } }
                .lift()


    private suspend fun fetchOrSkip(resultEntry: Any, excludeIds: Set<String>): Result<T?> =
        Either.catch {
            when (resultEntry) {
                is JSONObject -> resultEntry.get("index").toString()
                else -> throw RuntimeException("$resultEntry was not a json object")
            }
        }.suspendFlatMap{
            if(excludeIds.contains(it)) {
                Either.right(null)
            } else {
                loadResource(it)
            }
        }.mapLeft {
            println("encountered an error while loading resource ${resourceType}: $resultEntry")
            it
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


