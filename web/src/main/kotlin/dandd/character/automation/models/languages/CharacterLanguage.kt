
package dandd.character.automation.models.languages

import io.ktor.client.HttpClient
import web.api.ApiClient
import web.api.ClientParseError
import web.core.Either
import web.core.bindRight
import web.core.mapLeft
import web.parse.*
import kotlin.js.Json

data class CharacterLanguage(
    val _id: String,
    val index: String,
    val name: String,
    val desc: String?,
    val type: String,
    val typical_speakers: List<String>,
    val script: String?,
    val url: String
) {
    companion object {
        val parseResponseBody = { jsonString: String -> Either
            .catching { JSON.parse<Json>(jsonString) }
            .mapLeft { JsonParse(it) }
            .bindRight { from(it) }
            .mapLeft { ClientParseError(jsonString, it) }
        }
        
        fun from(json: Json?): Either<ParseError, CharacterLanguage> =
            json.parse {
                obj {
                    CharacterLanguage(
                        "_id".str(),
                        "index".str(),
                        "name".str(),
                        "desc".nullable {
                            "desc".str()
                        },
                        "type".str(),
                        "typical_speakers".arr {
                            "typical_speakers".str()
                        },
                        "script".nullable {
                            "script".str()
                        },
                        "url".str()
                    )
                }
            }
        
    fun client(httpClient: HttpClient): ApiClient<CharacterLanguage> =
            Client(httpClient)
            
    protected class Client(override val httpClient: HttpClient): ApiClient<CharacterLanguage> {
        override val parse = parseResponseBody
        
        suspend fun getMyClass(index: String) = 
            getResourceByUri("/api/languages/${index}")
    }

    }
}
