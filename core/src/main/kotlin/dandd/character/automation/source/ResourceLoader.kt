package dandd.character.automation.source

import dandd.character.automation.Result

interface ResourceLoader<T, ID: Any> {
    suspend fun loadAll(excludeIds: Set<ID>): List<Result<T>>
    suspend fun loadAll(): List<Result<T>>
    suspend fun loadResource(id: ID): Result<T>
}

fun <T> createLoaderFor(
        urlBase: String,
        resourcesBaseDirectory: String,
        resourceName: String,
        readingMapper: suspend (String) -> Result<T>,
        writingMapper: suspend (T) -> Result<String>,
        getId: suspend (T) -> Result<String>
): ResourceLoader<T, String> {
    val fileLoader = simpleFileReesourceLoader(resourceName, resourcesBaseDirectory, readingMapper, writingMapper)
    val httpLoader = PersistingOnSuccessResourceLoader(
            simpleHttpResourceLoader(urlBase, resourceName, readingMapper),
            fileLoader,
            getId)

    return MultiResourceLoader(
            listOf(
                    fileLoader,
                    httpLoader),
            getId)
}

//suspend fun <T, C> createLevelsLoader( urlBase: String,
//                                       resourcesBaseDirectory: String,
//                                       resourceName: String,
//                                       readingMapper: suspend (String) -> Result<T>,
//                                       writingMapper: suspend (T) -> Result<String>,
//                                       getId: suspend (T) -> Result<String>): (String, T) -> ResourceLoader<C>{
//    val subResourceName = "levels"
//    return { id, resource ->
//        createLoaderFor("$urlBase/api/$resourceName/$id/$subResourceName")
//    }
//
//}
