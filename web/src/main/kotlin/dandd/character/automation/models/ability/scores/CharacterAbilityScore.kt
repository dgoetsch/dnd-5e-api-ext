
package dandd.character.automation.models.ability.scores

import io.ktor.client.HttpClient
import web.api.ApiCient
import web.api.ClientParseError
import web.core.Either
import web.core.bindRight
import web.core.mapLeft
import web.parse.*
import kotlin.js.Json

data class CharacterAbilityScore(
    val _id: String,
    val index: String,
    val name: String,
    val full_name: String,
    val desc: List<String>,
    val skills: List<Skills>,
    val url: String
) {
    companion object {
        val parseResponseBody = { jsonString: String -> Either
            .catching { JSON.parse<Json>(jsonString) }
            .mapLeft { JsonParse(it) }
            .bindRight { from(it) }
            .mapLeft { ClientParseError(jsonString, it) }
        }
        
        fun from(json: Json?): Either<ParseError, CharacterAbilityScore> =
            json.parse {
                obj {
                    CharacterAbilityScore(
                        "_id".str(),
                        "index".str(),
                        "name".str(),
                        "full_name".str(),
                        "desc".arr {
                            str()
                        },
                        "skills".arr {
                            obj {
                                Skills.from(node).bind()
                            }
                        },
                        "url".str()
                    )
                }
            }
        
        fun client(httpClient: HttpClient): ApiCient<CharacterAbilityScore> =
            ApiCient(httpClient, "ability-scores", parseResponseBody)

    }
}
