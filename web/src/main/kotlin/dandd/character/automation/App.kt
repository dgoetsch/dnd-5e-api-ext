/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package dandd.character.automation

import arrow.core.Either
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dandd.character.automation.models.CharacterSpell
import dandd.character.automation.source.ResourceLoader
import dandd.character.automation.source.createLoaderFor
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.io.File
import io.ktor.application.*
import io.ktor.html.respondHtml
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
fun readResourceConfigDirectory() =
        System.getenv("API_RESOURCE_CONFIG_DIRECTORY")?:"api-resources"


fun main(args: Array<String>) {
    val urlBase = "https://www.dnd5eapi.co"
    val objectMapper = jacksonObjectMapper()
    val resourcesBaseDirectory = readResourcesDirectory()
    val resourceConfigBaseDir = readResourceConfigDirectory()

    val spellLoader = createLoaderFor(
            urlBase,
            resourcesBaseDirectory,
            "spells",
            { text -> Either.Companion.catch { objectMapper.readValue(text, CharacterSpell::class.java) } },
            { spell -> Either.catch { objectMapper.writeValueAsString(spell) } },
            { Either.Right(it.index) })

    val spells = runBlocking {
        ResourceOrigin(resourceConfigBaseDir, "spells", spellLoader)
                .loadAll()
                .catch { println(it) }
                .mapNotNull { when(it) {
                    is Either.Left<Throwable> -> {
                        println(it)
                        null
                    }
                    is Either.Right<CharacterSpell> -> it.b
                } }
                .fold(emptyList<CharacterSpell>()) { list, item -> list + item}
    }

    val port = 8080

    embeddedServer(Netty, port) {
        routing {
            get("/") {
                call.respondHtml {
                    render(MainPage("Vynne", spells))
                }
            }
        }
    }.start(wait = true)
}




data class ResourceOrigin<T>(val baseDir: String, val resourceType: String, val resourceLoader: ResourceLoader<T>) {
    suspend fun loadAll(): Flow<Result<T>> {
        val result = Either.catch {
            File("$baseDir/$resourceType.txt").useLines {
                it.toList().asFlow()
                        .flatMapConcat { resourceName ->
                            flow {
                                emit(resourceLoader.loadResource(resourceName))
                            }
                        }
            }
        }

        return when(result) {
            is Either.Left<Throwable> -> flowOf(result)
            is Either.Right<Flow<Result<T>>> -> result.b
        }
    }
}








