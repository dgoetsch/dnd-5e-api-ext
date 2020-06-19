package dandd.character.automation.source

import arrow.core.Either
import arrow.core.flatMap
import dandd.character.automation.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

internal data class PersistingOnSuccessResourceLoader<T>(val loader: ResourceLoader<T>,
                                                         val persister: ResourcePersister<T>,
                                                         val getName: suspend (T) -> Result<String>): ResourceLoader<T> {
    override suspend fun loadResource(name: String): Result<T> {
        val loadResult = loader.loadResource(name)

        return when(loadResult) {
            is Either.Left<Throwable> -> loadResult
            is Either.Right -> persister.persistResource(name, loadResult.b)
        }
    }

    override suspend fun loadAll(): List<Result<T>> =
        loadAll(emptySet())

    override suspend fun loadAll(excludeIds: Set<String>): List<Result<T>> =
        coroutineScope {
            loader.loadAll(excludeIds).map { either ->
                async {
                    when(either) {
                        is Either.Left -> either
                        is Either.Right -> getName(either.b).suspendFlatMap { name ->
                            persister.persistResource(name, either.b)
                        }
                    }
                }
            }.awaitAll()
        }
}