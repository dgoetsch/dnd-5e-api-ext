package dandd.character.automation

import arrow.core.Either
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dandd.character.automation.generator.KotlinClassWriter
import dandd.character.automation.generator.ModelRegistrar
import dandd.character.automation.generator.ModelTree
import dandd.character.automation.source.Resources
import dandd.character.automation.source.createLoaderFor
import dandd.character.automation.source.suspendFlatMap
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking


fun readTargetDirectory(): String {
    val taragetDirectory = System.getenv("GENERATED_KOTLIN_TARGET_DIRECTORY")
            ?: System.getProperty("generated.target.directory")
            ?: "src/main/kotlin"
    println("targetDirectory=$taragetDirectory")
    return taragetDirectory
}

fun main() {
    val objectMapper = jacksonObjectMapper()
    val targetDirectory = readTargetDirectory()

    val urlBase = "https://www.dnd5eapi.co"
    val pkg = "dandd.character.automation.models"


    val resources = Resources(objectMapper).dAndDResources()


    runBlocking {
        resources.map { (resourceName, className, _, getId) -> async {
            val pkg = "$pkg.${resourceName.toLowerCase().replace("[^a-z]+".toRegex(), ".")}"
            val registry = ModelRegistrar(pkg, resources)
            val loader = createLoaderFor(
                    urlBase,
                    readResourcesDirectory(),
                    resourceName,
                    { Either.Right(it) },
                    { Either.Right(it) },
                    getId
            )

            loader.loadAll()
                    .map {
                        async { it.suspendFlatMap { Either.catch {
                            val keyValues = it.toMap(objectMapper)
                            ModelTree(className, pkg, keyValues, registry)
                        } }
                        } }
                    .awaitAll()
            KotlinClassWriter(resourceName, className, pkg, registry.exportDictionary()).writeAll(targetDirectory)
        } }
                .awaitAll()
    }

}