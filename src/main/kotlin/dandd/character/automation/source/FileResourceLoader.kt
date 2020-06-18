package dandd.character.automation.source

import arrow.core.Either
import arrow.core.extensions.either.monad.flatten
import dandd.character.automation.Result
import java.io.File

internal data class FileResourceLoader<T>(
        val resourceType: String,
        val readingMapper: suspend (String) -> Result<T>,
        val writingMapper: suspend (T) -> Result<String>): ResourceLoader<T>, ResourcePersister<T> {
    override suspend fun persistResource(name: String, resource: T): Result<T> {
        val mapResult = writingMapper(resource)

        return when(mapResult) {
            is Either.Left<Throwable> -> mapResult
            is Either.Right<String> -> {
                val text = mapResult.b
                Either.catch {
                    File("$resourceType/$name.json").writeText(text)
                    resource
                }
            }
        }
    }

    override suspend fun loadResource(name: String): Result<T> = Either.catch {
        val text = File("$resourceType/$name.json").useLines { lines ->
            lines.joinToString("")
        }

        readingMapper(text)
    }.flatten()
}