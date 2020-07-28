package dandd.character.automation.source

import arrow.core.Either
import arrow.core.Right
import arrow.core.extensions.either.monad.flatten
import arrow.core.getOrElse
import arrow.core.right
import dandd.character.automation.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList
private fun String.escapeFileName() = this.replace("/", "&#47;")

internal fun <T>  simpleFileReesourceLoader( resourceType: String,
                             resourcesBaseDirectory: String,
                             readingMapper: suspend (String) -> Result<T>,
                             writingMapper: suspend (T) -> Result<String>): FileResourceLoader<T, String> {
    return FileResourceLoader(
            resourceType,
            resourcesBaseDirectory,
            readingMapper,
            writingMapper,
            { "${it.escapeFileName()}.json" },
            { Right(it.replace(".json", "")) }
    )
}
internal data class FileResourceLoader<T, ID: Any>(
        val resourceType: String,
        val resourcesBaseDirectory: String,
        val readingMapper: suspend (String) -> Result<T>,
        val writingMapper: suspend (T) -> Result<String>,
        val toFileName: (ID) -> String,
        val fromFileName: (String) -> Result<ID>): ResourceLoader<T, ID>, ResourcePersister<T, ID> {
    private fun getDirectory() = "$resourcesBaseDirectory/$resourceType"

    override suspend fun persistResource(name: ID, resource: T): Result<T> {
        val mapResult = writingMapper(resource)
        return when(mapResult) {
            is Either.Left<Throwable> -> mapResult
            is Either.Right<String> -> {
                val text = mapResult.b
                Either.catch {
                    File(getDirectory()).mkdirs()
                    File("${getDirectory()}/${toFileName(name)}").writeText(text)
                    resource
                }
            }
        }
    }


    override suspend fun loadResource(id: ID): Result<T> = Either.catch {
        val text = File("${getDirectory()}/${toFileName(id)}.json").useLines { lines ->
            lines.joinToString("")
        }

        readingMapper(text)
    }.flatten()

    override suspend fun loadAll(): List<Result<T>> = loadAll(emptySet())

    override suspend fun loadAll(excludeIds: Set<ID>): List<Result<T>> = coroutineScope {
        Either.catch { Files.walk(Paths.get(getDirectory())).toList() }
                .suspendFlatMap { Either.catch {
                    it.map { path -> async { Either
                            .catch {
                                if(path.toFile().isDirectory) null
                                else if(fromFileName(path.toFile().name).exists(excludeIds::contains)) null
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

}