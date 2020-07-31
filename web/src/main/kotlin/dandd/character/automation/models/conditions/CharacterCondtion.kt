
package dandd.character.automation.models.conditions

import io.ktor.client.HttpClient
import web.api.ApiClient
import web.api.ClientParseError
import web.core.Either
import web.core.bindRight
import web.core.mapLeft
import web.parse.*
import kotlin.js.Json

data class CharacterCondtion(
    val _id: String,
    val index: String,
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
        
        fun from(json: Json?): Either<ParseError, CharacterCondtion> =
            json.parse {
                obj {
                    CharacterCondtion(
                        "_id".str(),
                        "index".str(),
                        "name".str(),
                        "desc".arr {
                            "desc".str()
                        },
                        "url".str()
                    )
                }
            }
        
    fun client(httpClient: HttpClient): ApiClient<CharacterCondtion> =
            Client(httpClient)
            
    protected class Client(override val httpClient: HttpClient): ApiClient<CharacterCondtion> {
        override val parse = parseResponseBody
        
        suspend fun getMyClass(index: String) = 
            getResourceByUri("/api/conditions/${index}")
    }

    }
}
