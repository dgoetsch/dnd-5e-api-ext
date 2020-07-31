
package dandd.character.automation.models.races

import io.ktor.client.HttpClient
import web.api.ApiClient
import web.api.ClientParseError
import web.core.Either
import web.core.bindRight
import web.core.mapLeft
import web.parse.*
import kotlin.js.Json

data class CharacterRace(
    val _id: String,
    val index: String,
    val name: String,
    val speed: Int,
    val ability_bonuses: List<AbilityBonuses>,
    val alignment: String,
    val age: String,
    val size: String,
    val size_description: String,
    val starting_proficiencies: List<UrlName>,
    val languages: List<UrlName>,
    val language_desc: String,
    val traits: List<UrlName>,
    val subraces: List<UrlName>,
    val url: String,
    val starting_proficiency_options: ChooseTypeFrom?,
    val trait_options: ChooseTypeFrom?,
    val ability_bonus_options: AbilityBonusOptions?,
    val language_options: ChooseTypeFrom?
) {
    companion object {
        val parseResponseBody = { jsonString: String -> Either
            .catching { JSON.parse<Json>(jsonString) }
            .mapLeft { JsonParse(it) }
            .bindRight { from(it) }
            .mapLeft { ClientParseError(jsonString, it) }
        }
        
        fun from(json: Json?): Either<ParseError, CharacterRace> =
            json.parse {
                obj {
                    CharacterRace(
                        "_id".str(),
                        "index".str(),
                        "name".str(),
                        "speed".int(),
                        "ability_bonuses".arr {
                            "ability_bonuses".obj {
                                AbilityBonuses.from(node).bind()
                            }
                        },
                        "alignment".str(),
                        "age".str(),
                        "size".str(),
                        "size_description".str(),
                        "starting_proficiencies".arr {
                            "starting_proficiencies".obj {
                                UrlName.from(node).bind()
                            }
                        },
                        "languages".arr {
                            "languages".obj {
                                UrlName.from(node).bind()
                            }
                        },
                        "language_desc".str(),
                        "traits".arr {
                            "traits".obj {
                                UrlName.from(node).bind()
                            }
                        },
                        "subraces".arr {
                            "subraces".obj {
                                UrlName.from(node).bind()
                            }
                        },
                        "url".str(),
                        "starting_proficiency_options".nullable {
                            "starting_proficiency_options".obj {
                                ChooseTypeFrom.from(node).bind()
                            }
                        },
                        "trait_options".nullable {
                            "trait_options".obj {
                                ChooseTypeFrom.from(node).bind()
                            }
                        },
                        "ability_bonus_options".nullable {
                            "ability_bonus_options".obj {
                                AbilityBonusOptions.from(node).bind()
                            }
                        },
                        "language_options".nullable {
                            "language_options".obj {
                                ChooseTypeFrom.from(node).bind()
                            }
                        }
                    )
                }
            }
        
    fun client(httpClient: HttpClient): ApiClient<CharacterRace> =
            Client(httpClient)
            
    protected class Client(override val httpClient: HttpClient): ApiClient<CharacterRace> {
        override val parse = parseResponseBody
        
        suspend fun getMyClass(index: String) = 
            getResourceByUri("/api/races/${index}")
    }

    }
}
