
package dandd.character.automation.models.races

import io.ktor.client.HttpClient
import web.api.ApiClient
import web.api.ClientParseError
import web.core.Either
import web.core.bindRight
import web.core.mapLeft
import web.parse.*
import kotlin.js.Json

data class From(
    val url: String,
    val name: String,
    val bonus: Int?
) {
    companion object {
        val parseResponseBody = { jsonString: String -> Either
            .catching { JSON.parse<Json>(jsonString) }
            .mapLeft { JsonParse(it) }
            .bindRight { from(it) }
            .mapLeft { ClientParseError(jsonString, it) }
        }
        
        fun from(json: Json?): Either<ParseError, From> =
            json.parse {
                obj {
                    From(
                        "url".str(),
                        "name".str(),
                        "bonus".nullable {
                            int()
                        }
                    )
                }
            }
        
    }
}
