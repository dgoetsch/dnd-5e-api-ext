package dandd.character.automation.source

import dandd.character.automation.Result

interface ResourceLoader<T> {
    suspend fun loadResource(name: String): Result<T>
}

fun <T> createLoaderFor(
        urlBase: String,
        resourceName: String,
        readingMapper: suspend (String) -> Result<T>,
        writingMapper: suspend (T) -> Result<String>
): ResourceLoader<T> {
    val fileLoader = FileResourceLoader(resourceName, readingMapper, writingMapper)
    val httpLoader = PersistingOnSuccessResourceLoader(
            HttpResourceLoader(urlBase, resourceName, readingMapper),
            fileLoader)

    return MultiResourceLoader(
            listOf(
                    fileLoader,
                    httpLoader))
}