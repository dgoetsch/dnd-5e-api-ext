
package dandd.character.automation.models.equipment

import io.ktor.client.HttpClient
import web.api.ApiCient
import web.api.ClientParseError
import web.core.Either
import web.core.bindRight
import web.core.mapLeft
import web.parse.*
import kotlin.js.Json

data class Contents(
    val item_url: String,
    val quantity: Int
) {
    companion object {
        val parseResponseBody = { jsonString: String -> Either
            .catching { JSON.parse<Json>(jsonString) }
            .mapLeft { JsonParse(it) }
            .bindRight { from(it) }
            .mapLeft { ClientParseError(it) }
        }
        
        fun from(json: Json?): Either<ParseError, Contents> =
            json.parse {
                obj {
                    Contents(
                        "item_url".str(),
                        "quantity".int()
                    )
                }
            }
        
    }
}