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


fun readTargetDirectory() =
    System.getenv("GENERATED_KOTLIN_TARGET_DIRECTORY")?: "src/main/kotlin"

fun main() {
    val objectMapper = jacksonObjectMapper()
    val targetDirectory = readTargetDirectory()

    val urlBase = "https://www.dnd5eapi.co"
    val pkg = "dandd.character.automation.models"


    val resources = Resources(objectMapper).dAndDResources()
    val registry = ModelRegistrar(pkg, resources)

    runBlocking {
        resources.map { (resourceName, className, getId) -> async {
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
        } }
                .awaitAll()
                .flatten()
                .forEach {
                    when(it) {
                        is Either.Left -> {
                            println("Encountered an error ${it.a}")
                            null
                        }
                        is Either.Right -> it.b
                    }
                }


    }

    KotlinClassWriter(pkg, registry.exportDictionary()).writeAll(targetDirectory)
}