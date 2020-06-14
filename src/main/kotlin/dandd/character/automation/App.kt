/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package dandd.character.automation

import arrow.core.Either
import arrow.core.right
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dandd.character.automation.model.Spell
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
typealias Result<T> =  Either<Throwable, T>


fun main(args: Array<String>) {

    val urlBase = "https://www.dnd5eapi.co"
    val objectMapper = jacksonObjectMapper()

    val spellLoader = createLoaderFor(
            urlBase,
            "spells",
            { text -> Either.Companion.catch { objectMapper.readValue(text, Spell::class.java) } },
            { spell -> Either.catch { objectMapper.writeValueAsString(spell) } }
    )

    val spells = runBlocking {
        ResourceOrigin("spells", spellLoader)
                .loadAll()
                .catch { println(it) }
                .mapNotNull { when(it) {
                    is Either.Left<Throwable> -> null
                    is Either.Right<Spell> -> it.b
                } }
                .fold(emptyList<Spell>()) { list, item -> list + item}
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




data class ResourceOrigin<T>(val resourceType: String, val resourceLoader: ResourceLoader<T>) {
    suspend fun loadAll(): Flow<Result<T>> {
        val result = Either.catch {
            File("$resourceType.txt").useLines {
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








