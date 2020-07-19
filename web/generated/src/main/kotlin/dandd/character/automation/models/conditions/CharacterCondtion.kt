
package dandd.character.automation.models.conditions

import io.ktor.client.HttpClient
import web.api.ApiCient
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
            .mapLeft { ClientParseError(it) }
        }
        
        fun from(json: Json?): Either<ParseError, CharacterCondtion> =
            json.parse {
                obj {
                    CharacterCondtion(
                        "_id".str(),
                        "index".str(),
                        "name".str(),
                        "desc".arr {
                            str()
                        },
                        "url".str()
                    )
                }
            }
        
        fun client(httpClient: HttpClient): ApiCient<CharacterCondtion> =
            ApiCient(httpClient, "conditions", parseResponseBody)

    }
}
