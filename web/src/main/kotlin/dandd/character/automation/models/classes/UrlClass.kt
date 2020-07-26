
package dandd.character.automation.models.classes

import io.ktor.client.HttpClient
import web.api.ApiCient
import web.api.ClientParseError
import web.core.Either
import web.core.bindRight
import web.core.mapLeft
import web.parse.*
import kotlin.js.Json

data class UrlClass(
    val url: String,
    val `class`: String
) {
    companion object {
        val parseResponseBody = { jsonString: String -> Either
            .catching { JSON.parse<Json>(jsonString) }
            .mapLeft { JsonParse(it) }
            .bindRight { from(it) }
            .mapLeft { ClientParseError(jsonString, it) }
        }
        
        fun from(json: Json?): Either<ParseError, UrlClass> =
            json.parse {
                obj {
                    UrlClass(
                        "url".str(),
                        "class".str()
                    )
                }
            }
        
    }
}
