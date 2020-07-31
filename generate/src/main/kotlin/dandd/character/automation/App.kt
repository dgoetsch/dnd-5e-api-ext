package dandd.character.automation

import arrow.core.Either
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dandd.character.automation.generator.KotlinClassWriter
import dandd.character.automation.generator.ModelRegistrar
import dandd.character.automation.generator.ModelTree
import dandd.character.automation.generator.clientWriterConfig
import dandd.character.automation.source.ResourceLoaderFactory
import dandd.character.automation.source.Resources
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
    val targetDirectory = readTargetDirectory()
    val urlBase = "https://www.dnd5eapi.co"
    val factory = ResourceLoaderFactory(urlBase, readResourcesDirectory())

    val objectMapper = jacksonObjectMapper()
    val pkg = "dandd.character.automation.models"


    val resources = Resources(objectMapper).dndResources(factory)

    val nestedResources = Resources(objectMapper).dndNestedResources(factory)
    runBlocking {

    }

    runBlocking {
        val results = nestedResources.map { (resourceName, className, loader) -> async {
            val pkg = "$pkg.${resourceName.toLowerCase().replace("[^a-z]+".toRegex(), ".")}"
            val registry = ModelRegistrar(pkg, resources)

            loader.loadAll()
                    .map {
                        async { it.suspendFlatMap { Either.catch {
                            val keyValues = it.toMap(objectMapper)
                            ModelTree(className, pkg, keyValues, registry)
                        } }
                        } }
                    .awaitAll()
            KotlinClassWriter(className, pkg, registry.exportDictionary(), clientWriterConfig(resourceName.split("-"))).writeAll(targetDirectory)
        } } + resources.map { (resourceName, className, loader) -> async {
            val pkg = "$pkg.${resourceName.toLowerCase().replace("[^a-z]+".toRegex(), ".")}"
            val registry = ModelRegistrar(pkg, resources)

            loader.loadAll()
                    .map {
                        async { it.suspendFlatMap { Either.catch {
                            val keyValues = it.toMap(objectMapper)
                            ModelTree(className, pkg, keyValues, registry)
                        } }
                        } }
                    .awaitAll()
            KotlinClassWriter(className, pkg, registry.exportDictionary(), clientWriterConfig(resourceName)).writeAll(targetDirectory)
        } }

        results.awaitAll()
    }



}