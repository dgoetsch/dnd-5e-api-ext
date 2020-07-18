import dandd.character.automation.models.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import web.parse.*
import react.dom.*
import web.api.ApiCient
import web.api.ClientParseError
import web.core.*
import kotlin.browser.document
import kotlin.js.Json



fun Parser<Any?>.readNameUrl() =
    NameUrl(
            readStringField("name"),
            readStringField("url")
    )
fun Parser<Json?>.readUrlClass() =
        UrlClass(
                readStringField("url"),
                readStringField("class")
        )
val parseThing = { responseBody: String ->
    Either.catching { JSON.parse<Json>(responseBody) }
            .mapLeft { JsonParse(it) }
            .bindRight { it.parse {
                CharacterClass(
                        readStringField("_id"),
                        readStringField("index"),
                        readStringField("name"),
                        readIntField("hit_die"),
                        readArrayField("proficiency_choices") {
                            ChooseTypeFrom(
                                    readIntField("choose"),
                                    readStringField("type"),
                                    readArrayField("from") {
                                        readNameUrl()
                                    }
                            )
                        },
                        readArrayField("proficiencies") {
                            readNameUrl()
                        },
                        readArrayField("saving_throws") {
                            readNameUrl()
                        },
                        readObjectField("starting_equipment") { readUrlClass() },
                        readObjectField("class_levels") { readUrlClass() },
                        readArrayField("subclasses") {
                            readNameUrl()
                        },
                        readStringField("url"),
                        readObjectField("spellCasting") {
                            nullable {
                                readUrlClass()
                            }
                        })
                }
            }
            .mapLeft { ClientParseError(it) }
}


fun main() {

    val client = HttpClient(Js) {
    }
    val mainScope = MainScope()
    mainScope.launch {
        val resource = ApiCient(client).getResource("classes", "fighter", parseThing)
        println(resource)
    }
    render(document.getElementById("root")) {
        h1 {
            +"Hello, React+Kotlin/JS!"

        }
    }
//    val urlBase = "https://www.dnd5eapi.co"
//    val objectMapper = jacksonObjectMapper()
//    val resourcesBaseDirectory = readResourcesDirectory()
//    val resourceConfigBaseDir = readResourceConfigDirectory()
//
//    val spellLoader = createLoaderFor(
//            urlBase,
//            resourcesBaseDirectory,
//            "spells",
//            { text -> Either.Companion.catch { objectMapper.readValue(text, CharacterSpell::class.java) } },
//            { spell -> Either.catch { objectMapper.writeValueAsString(spell) } },
//            { Either.Right(it.index) })
//
//    val spells = runBlocking {
//        ResourceOrigin(resourceConfigBaseDir, "spells", spellLoader)
//                .loadAll()
//                .catch { println(it) }
//                .mapNotNull { when(it) {
//                    is Either.Left<Throwable> -> {
//                        println(it)
//                        null
//                    }
//                    is Either.Right<CharacterSpell> -> it.b
//                } }
//                .fold(emptyList<CharacterSpell>()) { list, item -> list + item}
//    }
//
//    val port = 8080
//
//    embeddedServer(Netty, port) {
//        routing {
//            get("/") {
//                call.respondHtml {
//                    render(MainPage("Vynne", spells))
//                }
//            }
//        }
//    }.start(wait = true)
}
//
//
//
//
//data class ResourceOrigin<T>(val baseDir: String, val resourceType: String, val resourceLoader: ResourceLoader<T>) {
//    suspend fun loadAll(): Flow<Result<T>> {
//        val result = Either.catch {
//            File("$baseDir/$resourceType.txt").useLines {
//                it.toList().asFlow()
//                        .flatMapConcat { resourceName ->
//                            flow {
//                                emit(resourceLoader.loadResource(resourceName))
//                            }
//                        }
//            }
//        }
//
//        return when(result) {
//            is Either.Left<Throwable> -> flowOf(result)
//            is Either.Right<Flow<Result<T>>> -> result.b
//        }
//    }
//}
//
//
//
//
//
//
//
//
