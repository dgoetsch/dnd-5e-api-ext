package dandd.character.automation.source

import arrow.core.Either
import dandd.character.automation.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

internal data class PersistingOnSuccessResourceLoader<T>(val loader: ResourceLoader<T>,
                                                         val persister: ResourcePersister<T>,
                                                         val getId: suspend (T) -> Result<String>): ResourceLoader<T> {
    override suspend fun loadResource(id: String): Result<T> {
        val loadResult = loader.loadResource(id)

        return when(loadResult) {
            is Either.Left<Throwable> -> loadResult
            is Either.Right -> persister.persistResource(id, loadResult.b)
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
                        is Either.Right -> getId(either.b).suspendFlatMap { name ->
                            persister.persistResource(name, either.b)
                        }
                    }
                }
            }.awaitAll()
        }
}