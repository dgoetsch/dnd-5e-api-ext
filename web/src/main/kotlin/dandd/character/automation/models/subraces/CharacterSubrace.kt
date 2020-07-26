
package dandd.character.automation.models.subraces

import io.ktor.client.HttpClient
import web.api.ApiCient
import web.api.ClientParseError
import web.core.Either
import web.core.bindRight
import web.core.mapLeft
import web.parse.*
import kotlin.js.Json

data class CharacterSubrace(
    val _id: String,
    val index: String,
    val name: String,
    val race: UrlName,
    val desc: String,
    val ability_bonuses: List<UrlName>,
    val starting_proficiencies: List<UrlName>,
    val languages: List<UrlName>,
    val language_options: ChooseFromType?,
    val racial_traits: List<UrlName>,
    val racial_trait_options: ChooseFromType?,
    val url: String
) {
    companion object {
        val parseResponseBody = { jsonString: String -> Either
            .catching { JSON.parse<Json>(jsonString) }
            .mapLeft { JsonParse(it) }
            .bindRight { from(it) }
            .mapLeft { ClientParseError(it) }
        }
        
        fun from(json: Json?): Either<ParseError, CharacterSubrace> =
            json.parse {
                obj {
                    CharacterSubrace(
                        "_id".str(),
                        "index".str(),
                        "name".str(),
                        "race".obj {
                            UrlName.from(node).bind()
                        },
                        "desc".str(),
                        "ability_bonuses".arr {
                            obj {
                                UrlName.from(node).bind()
                            }
                        },
                        "starting_proficiencies".arr {
                            obj {
                                UrlName.from(node).bind()
                            }
                        },
                        "languages".arr {
                            obj {
                                UrlName.from(node).bind()
                            }
                        },
                        "language_options".nullable {
                            obj {
                                ChooseFromType.from(node).bind()
                            }
                        },
                        "racial_traits".arr {
                            obj {
                                UrlName.from(node).bind()
                            }
                        },
                        "racial_trait_options".nullable {
                            obj {
                                ChooseFromType.from(node).bind()
                            }
                        },
                        "url".str()
                    )
                }
            }
        
        fun client(httpClient: HttpClient): ApiCient<CharacterSubrace> =
            ApiCient(httpClient, "subraces", parseResponseBody)

    }
}
