
package dandd.character.automation.models.classes.levels

import io.ktor.client.HttpClient
import web.api.ApiClient
import web.api.ClientParseError
import web.core.Either
import web.core.bindRight
import web.core.mapLeft
import web.parse.*
import kotlin.js.Json

data class Spellcasting(
    val spells_known: Int?,
    val spell_slots_level_1: Int,
    val spell_slots_level_2: Int,
    val spell_slots_level_3: Int,
    val spell_slots_level_4: Int,
    val spell_slots_level_5: Int,
    val cantrips_known: Int?,
    val spell_slots_level_6: Int?,
    val spell_slots_level_7: Int?,
    val spell_slots_level_8: Int?,
    val spell_slots_level_9: Int?
) {
    companion object {
        val parseResponseBody = { jsonString: String -> Either
            .catching { JSON.parse<Json>(jsonString) }
            .mapLeft { JsonParse(it) }
            .bindRight { from(it) }
            .mapLeft { ClientParseError(jsonString, it) }
        }
        
        fun from(json: Json?): Either<ParseError, Spellcasting> =
            json.parse {
                obj {
                    Spellcasting(
                        "spells_known".nullable {
                            "spells_known".int()
                        },
                        "spell_slots_level_1".int(),
                        "spell_slots_level_2".int(),
                        "spell_slots_level_3".int(),
                        "spell_slots_level_4".int(),
                        "spell_slots_level_5".int(),
                        "cantrips_known".nullable {
                            "cantrips_known".int()
                        },
                        "spell_slots_level_6".nullable {
                            "spell_slots_level_6".int()
                        },
                        "spell_slots_level_7".nullable {
                            "spell_slots_level_7".int()
                        },
                        "spell_slots_level_8".nullable {
                            "spell_slots_level_8".int()
                        },
                        "spell_slots_level_9".nullable {
                            "spell_slots_level_9".int()
                        }
                    )
                }
            }
        
    }
}
