package dandd.character.automation.source

import dandd.character.automation.Result

interface ResourceLoader<T> {
    suspend fun loadAll(excludeIds: Set<String>): List<Result<T>>
    suspend fun loadAll(): List<Result<T>>
    suspend fun loadResource(name: String): Result<T>
}

fun <T> createLoaderFor(
        urlBase: String,
        resourcesBaseDirectory: String,
        resourceName: String,
        readingMapper: suspend (String) -> Result<T>,
        writingMapper: suspend (T) -> Result<String>,
        getName: suspend (T) -> Result<String>,
        getId: suspend (T) -> Result<String>
): ResourceLoader<T> {
    val fileLoader = FileResourceLoader(resourceName, resourcesBaseDirectory, readingMapper, writingMapper, getId)
    val httpLoader = PersistingOnSuccessResourceLoader(
            HttpResourceLoader(urlBase, resourceName, readingMapper),
            fileLoader,
            getName)

    return MultiResourceLoader(
            listOf(
                    fileLoader,
                    httpLoader),
            getId)
}