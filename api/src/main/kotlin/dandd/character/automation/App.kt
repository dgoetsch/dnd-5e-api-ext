package dandd.character.automation

import arrow.core.Either
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dandd.character.automation.source.Resources
import dandd.character.automation.source.createLoaderFor
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondBytes
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.coroutineScope


fun main() {
    val port = 8099
    val objectMapper = jacksonObjectMapper()
    val resources = Resources(objectMapper).dAndDResources()
    val urlBase = "https://www.dnd5eapi.co"

    val loaders = resources
            .map { (name, _, childResources, getId) ->
                name to createLoaderFor(
                        urlBase,
                        readResourcesDirectory(),
                        name,
                        { Either.Right(it) },
                        { Either.Right(it) },
                        getId)
            }
            .toMap()

    embeddedServer(Netty, port) {
        install(DefaultHeaders) {
            header("Access-Control-Allow-Origin", "http://localhost:8080")
            header("Access-Control-Allow-Credentials", "true")
        }
        routing {
            get("/api/{resource}") {
                call.parameters.get("resource")
                        ?.let { resource ->
                            val response = khttp.get("$urlBase/api/$resource")
                            call.respondBytes(response.content, response.headers.get("Content-Type")?.let {
                                ContentType.parse(it) }?:ContentType.Application.Json, HttpStatusCode.fromValue(response.statusCode))
                        }?:call.respond(HttpStatusCode.NotFound)
            }

            get("/api/{resource}/{id}") {
                call.parameters.get("resource")
                        ?.let(loaders::get)
                        ?.let {loader ->
                            call.parameters.get("id")?.let { id -> coroutineScope {
                                val resource = loader.loadResource(id)
                                when(resource) {
                                    is Either.Right -> call.respondText(resource.b, ContentType.Application.Json)
                                    is Either.Left -> call.respondText(resource.a.message?:"Error", ContentType.Text.Any, HttpStatusCode.InternalServerError)
                                }
                            } }
                        }?: call.respond(HttpStatusCode.NotFound)
            }
        }
    }.start(wait = true)
}