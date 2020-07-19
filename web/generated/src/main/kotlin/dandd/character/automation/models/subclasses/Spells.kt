
package dandd.character.automation.models.subclasses

import io.ktor.client.HttpClient
import web.api.ApiCient
import web.api.ClientParseError
import web.core.Either
import web.core.bindRight
import web.core.mapLeft
import web.parse.*
import kotlin.js.Json

data class Spells(
    val prerequisites: List<UrlName>,
    val spell: UrlName
) {
    companion object {
        val parseResponseBody = { jsonString: String -> Either
            .catching { JSON.parse<Json>(jsonString) }
            .mapLeft { JsonParse(it) }
            .bindRight { from(it) }
            .mapLeft { ClientParseError(it) }
        }
        
        fun from(json: Json?): Either<ParseError, Spells> =
            json.parse {
                obj {
                    Spells(
                        "prerequisites".arr {
                            obj {
                                UrlName.from(node).bind()
                            }
                        },
                        "spell".obj {
                            UrlName.from(node).bind()
                        }
                    )
                }
            }
        
    }
}
