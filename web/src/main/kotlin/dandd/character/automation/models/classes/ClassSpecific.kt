
package dandd.character.automation.models.classes

import io.ktor.client.HttpClient
import web.api.ApiClient
import web.api.ClientParseError
import web.core.Either
import web.core.bindRight
import web.core.mapLeft
import web.parse.*
import kotlin.js.Json

data class ClassSpecific(
    val rage_count: Int?,
    val rage_damage_bonus: Int?,
    val brutal_critical_dice: Int?,
    val favored_enemies: Int?,
    val favored_terrain: Int?,
    val sorcery_points: Int?,
    val metamagic_known: Int?,
    val creating_spell_slots: List<CreatingSpellSlots>?,
    val action_surges: Int?,
    val indomitable_uses: Int?,
    val extra_attacks: Int?,
    val invocations_known: Int?,
    val mystic_arcanum_level_6: Int?,
    val mystic_arcanum_level_7: Int?,
    val mystic_arcanum_level_8: Int?,
    val mystic_arcanum_level_9: Int?,
    val martial_arts: DiceCountDiceValue?,
    val ki_points: Int?,
    val unarmored_movement: Int?,
    val bardic_inspiration_die: Int?,
    val song_of_rest_die: Int?,
    val magical_secrets_max_5: Int?,
    val magical_secrets_max_7: Int?,
    val magical_secrets_max_9: Int?,
    val aura_range: Int?,
    val wild_shape_max_cr: Double?,
    val wild_shape_swim: Boolean?,
    val wild_shape_fly: Boolean?,
    val arcane_recovery_levels: Int?,
    val sneak_attack: DiceCountDiceValue?,
    val channel_divinity_charges: Int?,
    val destroy_undead_cr: Double?
) {
    companion object {
        val parseResponseBody = { jsonString: String -> Either
            .catching { JSON.parse<Json>(jsonString) }
            .mapLeft { JsonParse(it) }
            .bindRight { from(it) }
            .mapLeft { ClientParseError(jsonString, it) }
        }
        
        fun from(json: Json?): Either<ParseError, ClassSpecific> =
            json.parse {
                obj {
                    ClassSpecific(
                        "rage_count".nullable {
                            "rage_count".int()
                        },
                        "rage_damage_bonus".nullable {
                            "rage_damage_bonus".int()
                        },
                        "brutal_critical_dice".nullable {
                            "brutal_critical_dice".int()
                        },
                        "favored_enemies".nullable {
                            "favored_enemies".int()
                        },
                        "favored_terrain".nullable {
                            "favored_terrain".int()
                        },
                        "sorcery_points".nullable {
                            "sorcery_points".int()
                        },
                        "metamagic_known".nullable {
                            "metamagic_known".int()
                        },
                        "creating_spell_slots".nullable {
                            "creating_spell_slots".arr {
                                "creating_spell_slots".obj {
                                    CreatingSpellSlots.from(node).bind()
                                }
                            }
                        },
                        "action_surges".nullable {
                            "action_surges".int()
                        },
                        "indomitable_uses".nullable {
                            "indomitable_uses".int()
                        },
                        "extra_attacks".nullable {
                            "extra_attacks".int()
                        },
                        "invocations_known".nullable {
                            "invocations_known".int()
                        },
                        "mystic_arcanum_level_6".nullable {
                            "mystic_arcanum_level_6".int()
                        },
                        "mystic_arcanum_level_7".nullable {
                            "mystic_arcanum_level_7".int()
                        },
                        "mystic_arcanum_level_8".nullable {
                            "mystic_arcanum_level_8".int()
                        },
                        "mystic_arcanum_level_9".nullable {
                            "mystic_arcanum_level_9".int()
                        },
                        "martial_arts".nullable {
                            "martial_arts".obj {
                                DiceCountDiceValue.from(node).bind()
                            }
                        },
                        "ki_points".nullable {
                            "ki_points".int()
                        },
                        "unarmored_movement".nullable {
                            "unarmored_movement".int()
                        },
                        "bardic_inspiration_die".nullable {
                            "bardic_inspiration_die".int()
                        },
                        "song_of_rest_die".nullable {
                            "song_of_rest_die".int()
                        },
                        "magical_secrets_max_5".nullable {
                            "magical_secrets_max_5".int()
                        },
                        "magical_secrets_max_7".nullable {
                            "magical_secrets_max_7".int()
                        },
                        "magical_secrets_max_9".nullable {
                            "magical_secrets_max_9".int()
                        },
                        "aura_range".nullable {
                            "aura_range".int()
                        },
                        "wild_shape_max_cr".nullable {
                            "wild_shape_max_cr".double()
                        },
                        "wild_shape_swim".nullable {
                            "wild_shape_swim".boolean()
                        },
                        "wild_shape_fly".nullable {
                            "wild_shape_fly".boolean()
                        },
                        "arcane_recovery_levels".nullable {
                            "arcane_recovery_levels".int()
                        },
                        "sneak_attack".nullable {
                            "sneak_attack".obj {
                                DiceCountDiceValue.from(node).bind()
                            }
                        },
                        "channel_divinity_charges".nullable {
                            "channel_divinity_charges".int()
                        },
                        "destroy_undead_cr".nullable {
                            "destroy_undead_cr".double()
                        }
                    )
                }
            }
        
    }
}
