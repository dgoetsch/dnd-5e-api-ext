package dandd.character.automation.source

import arrow.core.Either
import dandd.character.automation.Result

internal data class PersistingOnSuccessResourceLoader<T>(val loader: ResourceLoader<T>,
                                                val persister: ResourcePersister<T>): ResourceLoader<T> {
    override suspend fun loadResource(name: String): Result<T> {
        val loadResult = loader.loadResource(name)

        return when(loadResult) {
            is Either.Left<Throwable> -> loadResult
            is Either.Right -> persister.persistResource(name, loadResult.b)
        }
    }
}