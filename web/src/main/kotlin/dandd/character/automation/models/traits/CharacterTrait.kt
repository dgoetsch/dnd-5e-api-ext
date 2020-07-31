
package dandd.character.automation.models.traits

import io.ktor.client.HttpClient
import web.api.ApiClient
import web.api.ClientParseError
import web.core.Either
import web.core.bindRight
import web.core.mapLeft
import web.parse.*
import kotlin.js.Json

data class CharacterTrait(
    val _id: String,
    val index: String,
    val races: List<NameUrl>,
    val subraces: List<NameUrl>,
    val name: String,
    val desc: List<String>,
    val url: String
) {
    companion object {
        val parseResponseBody = { jsonString: String -> Either
            .catching { JSON.parse<Json>(jsonString) }
            .mapLeft { JsonParse(it) }
            .bindRight { from(it) }
            .mapLeft { ClientParseError(jsonString, it) }
        }
        
        fun from(json: Json?): Either<ParseError, CharacterTrait> =
            json.parse {
                obj {
                    CharacterTrait(
                        "_id".str(),
                        "index".str(),
                        "races".arr {
                            "races".obj {
                                NameUrl.from(node).bind()
                            }
                        },
                        "subraces".arr {
                            "subraces".obj {
                                NameUrl.from(node).bind()
                            }
                        },
                        "name".str(),
                        "desc".arr {
                            "desc".str()
                        },
                        "url".str()
                    )
                }
            }
        
    fun client(httpClient: HttpClient): ApiClient<CharacterTrait> =
            Client(httpClient)
            
    protected class Client(override val httpClient: HttpClient): ApiClient<CharacterTrait> {
        override val parse = parseResponseBody
        
        suspend fun getMyClass(index: String) = 
            getResourceByUri("/api/traits/${index}")
    }

    }
}
