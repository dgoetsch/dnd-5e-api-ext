
package dandd.character.automation.models.classes.levels

import io.ktor.client.HttpClient
import web.api.ApiCient
import web.api.ClientParseError
import web.core.Either
import web.core.bindRight
import web.core.mapLeft
import web.parse.*
import kotlin.js.Json

data class CharacterClassLevel(
    val _id: String,
    val level: Int,
    val ability_score_bonuses: Int,
    val prof_bonus: Int,
    val feature_choices: List<NameUrl>,
    val features: List<NameUrl>,
    val spellcasting: ClassSpecific?,
    val class_specific: Map<String, Any>,
    val index: Int,
    val `class`: NameUrl,
    val url: String
) {
    companion object {
        val parseResponseBody = { jsonString: String -> Either
            .catching { JSON.parse<Json>(jsonString) }
            .mapLeft { JsonParse(it) }
            .bindRight { from(it) }
            .mapLeft { ClientParseError(jsonString, it) }
        }
        
        fun from(json: Json?): Either<ParseError, CharacterClassLevel> =
            json.parse {
                obj {
                    CharacterClassLevel(
                        "_id".str(),
                        "level".int(),
                        "ability_score_bonuses".int(),
                        "prof_bonus".int(),
                        "feature_choices".arr {
                            obj {
                                NameUrl.from(node).bind()
                            }
                        },
                        "features".arr {
                            obj {
                                NameUrl.from(node).bind()
                            }
                        },
                        "spellcasting".nullable {
                            obj {
                                ClassSpecific.from(node).bind()
                            }
                        },
                        "class_specific".obj {
                            "rage_count".int()
                            "rage_damage_bonus".int()
                            "brutal_critical_dice".int()
                        },
                        "index".int(),
                        "class".obj {
                            NameUrl.from(node).bind()
                        },
                        "url".str()
                    )
                }
            }
        
        fun client(httpClient: HttpClient): ApiCient<CharacterClassLevel> =
            ApiCient(httpClient, "classes-levels", parseResponseBody)

    }
}
