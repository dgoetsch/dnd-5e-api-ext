
package dandd.character.automation.models.races

import io.ktor.client.HttpClient
import web.api.ApiCient
import web.api.ClientParseError
import web.core.Either
import web.core.bindRight
import web.core.mapLeft
import web.parse.*
import kotlin.js.Json

data class ChooseTypeFrom(
    val choose: Int,
    val type: String,
    val from: List<UrlName>
) {
    companion object {
        val parseResponseBody = { jsonString: String -> Either
            .catching { JSON.parse<Json>(jsonString) }
            .mapLeft { JsonParse(it) }
            .bindRight { from(it) }
            .mapLeft { ClientParseError(it) }
        }
        
        fun from(json: Json?): Either<ParseError, ChooseTypeFrom> =
            json.parse {
                obj {
                    ChooseTypeFrom(
                        "choose".int(),
                        "type".str(),
                        "from".arr {
                            obj {
                                UrlName.from(node).bind()
                            }
                        }
                    )
                }
            }
        
    }
}
