package dandd.character.automation.source

import arrow.core.Either
import arrow.core.extensions.either.monad.flatten
import arrow.core.getOrElse
import dandd.character.automation.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList

internal data class FileResourceLoader<T>(
        val resourceType: String,
        val resourcesBaseDirectory: String,
        val readingMapper: suspend (String) -> Result<T>,
        val writingMapper: suspend (T) -> Result<String>,
        val getId: suspend (T) -> Result<String>): ResourceLoader<T>, ResourcePersister<T> {
    private fun getDirectory() = "$resourcesBaseDirectory/$resourceType"

    override suspend fun persistResource(name: String, resource: T): Result<T> {
        val mapResult = writingMapper(resource)
        return when(mapResult) {
            is Either.Left<Throwable> -> mapResult
            is Either.Right<String> -> {
                val text = mapResult.b
                Either.catch {
                    File(getDirectory()).mkdirs()
                    File("${getDirectory()}/${name.escapeFileName()}.json").writeText(text)
                    resource
                }
            }
        }
    }


    override suspend fun loadResource(id: String): Result<T> = Either.catch {
        val text = File("${getDirectory()}/${id.escapeFileName()}.json").useLines { lines ->
            lines.joinToString("")
        }

        readingMapper(text)
    }.flatten()

    override suspend fun loadAll(): List<Result<T>> = loadAll(emptySet())

    override suspend fun loadAll(excludeIds: Set<String>): List<Result<T>> = coroutineScope {
        Either.catch { Files.walk(Paths.get(getDirectory())).toList() }
                .suspendFlatMap { Either.catch {
                    it.map { path -> async { Either
                            .catch {
                                if(path.toFile().isDirectory) null
                                else if(excludeIds.contains(path.toFile().name.replace(".json", ""))) null
                                else Files.readString(path)
                            }
                            .suspendFlatMapNotNull(readingMapper)
                            .mapLeft {
                                println("Encountered an error while loading file: ${path.toAbsolutePath()}")
                                it
                            }
                    } }.awaitAll()
                } }
                .lift()
                .filterRightNotNull()
    }

    private fun String.escapeFileName() = this.replace("/", "&#47;")
}