
package dandd.character.automation.models.languages

import io.ktor.client.HttpClient
import web.api.ApiCient
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
            .mapLeft { ClientParseError(it) }
        }
        
        fun from(json: Json?): Either<ParseError, CharacterLanguage> =
            json.parse {
                obj {
                    CharacterLanguage(
                        "_id".str(),
                        "index".str(),
                        "name".str(),
                        "desc".nullable {
                            str()
                        },
                        "type".str(),
                        "typical_speakers".arr {
                            str()
                        },
                        "script".nullable {
                            str()
                        },
                        "url".str()
                    )
                }
            }
        
        fun client(httpClient: HttpClient): ApiCient<CharacterLanguage> =
            ApiCient(httpClient, "languages", parseResponseBody)

    }
}
