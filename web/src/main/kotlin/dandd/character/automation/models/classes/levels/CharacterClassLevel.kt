
package dandd.character.automation.models.classes.levels

import io.ktor.client.HttpClient
import web.api.ApiClient
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
    val class_specific: Map<String, Any>,
    val index: Int,
    val `class`: NameUrl,
    val url: String,
    val spellcasting: ClassSpecific?
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
                        "class_specific".obj {
                            "sorcery_points".int()
                            "metamagic_known".int()
                            "creating_spell_slots".arr {
                                obj {
                                    CreatingSpellSlots.from(node).bind()
                                }
                            }
                        },
                        "index".int(),
                        "class".obj {
                            NameUrl.from(node).bind()
                        },
                        "url".str(),
                        "spellcasting".nullable {
                            obj {
                                ClassSpecific.from(node).bind()
                            }
                        }
                    )
                }
            }
        
    fun client(httpClient: HttpClient): ApiClient<CharacterClassLevel> =
            Client(httpClient)
            
    protected class Client(override val httpClient: HttpClient): ApiClient<CharacterClassLevel> {
        override val parse = parseResponseBody
        
        suspend fun getMyClass(classes: String, levels: String) = 
            getResourceByUri("/api/classes/${classes}/levels/${levels}")
    }

    }
}
