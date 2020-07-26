
package dandd.character.automation.models.subclasses

import io.ktor.client.HttpClient
import web.api.ApiCient
import web.api.ClientParseError
import web.core.Either
import web.core.bindRight
import web.core.mapLeft
import web.parse.*
import kotlin.js.Json

data class Prerequisites(
    val url: String,
    val type: String?,
    val name: String?
) {
    companion object {
        val parseResponseBody = { jsonString: String -> Either
            .catching { JSON.parse<Json>(jsonString) }
            .mapLeft { JsonParse(it) }
            .bindRight { from(it) }
            .mapLeft { ClientParseError(jsonString, it) }
        }
        
        fun from(json: Json?): Either<ParseError, Prerequisites> =
            json.parse {
                obj {
                    Prerequisites(
                        "url".str(),
                        "type".nullable {
                            str()
                        },
                        "name".nullable {
                            str()
                        }
                    )
                }
            }
        
    }
}
