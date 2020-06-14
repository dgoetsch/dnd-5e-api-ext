package dandd.character.automation.source

import arrow.core.Either
import dandd.character.automation.Result
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.fold

internal data class MultiResourceLoader<T>(val loaders: List<ResourceLoader<T>>): ResourceLoader<T> {
    companion object {
        object NoLoaders: Throwable("No Loaders")
        private fun <T> initialResult(): Either<Throwable, T> =
                Either.left(NoLoaders)
    }


    override suspend fun loadResource(name: String): Result<T> {
        println("loading $name")
        return loaders.asFlow().fold(initialResult()) { ongoing, loader ->
            when (ongoing) {
                is Either.Left<Throwable> -> loader.loadResource(name)
                is Either.Right<T> -> ongoing
            }
        }
    }
}