
package dandd.character.automation.models.equipment

import io.ktor.client.HttpClient
import web.api.ApiClient
import web.api.ClientParseError
import web.core.Either
import web.core.bindRight
import web.core.mapLeft
import web.parse.*
import kotlin.js.Json

data class CharacterEquipment(
    val _id: String,
    val index: String,
    val name: String,
    val equipment_category: NameUrl,
    val gear_category: String?,
    val cost: Cost,
    val weight: Double?,
    val url: String,
    val weapon_category: String?,
    val weapon_range: String?,
    val category_range: String?,
    val damage: DamageDiceDamageBonusDamageType?,
    val range: Range?,
    val properties: List<NameUrl>?,
    val tool_category: String?,
    val desc: List<String>?,
    val vehicle_category: String?,
    val speed: Cost?,
    val _2h_damage: DamageDiceDamageBonusDamageType?,
    val contents: List<Contents>?,
    val capacity: String?,
    val armor_category: String?,
    val armor_class: ArmorClass?,
    val str_minimum: Int?,
    val stealth_disadvantage: Boolean?,
    val quantity: Int?,
    val throw_range: ThrowRange?,
    val special: List<String>?
) {
    companion object {
        val parseResponseBody = { jsonString: String -> Either
            .catching { JSON.parse<Json>(jsonString) }
            .mapLeft { JsonParse(it) }
            .bindRight { from(it) }
            .mapLeft { ClientParseError(jsonString, it) }
        }
        
        fun from(json: Json?): Either<ParseError, CharacterEquipment> =
            json.parse {
                obj {
                    CharacterEquipment(
                        "_id".str(),
                        "index".str(),
                        "name".str(),
                        "equipment_category".obj {
                            NameUrl.from(node).bind()
                        },
                        "gear_category".nullable {
                            "gear_category".str()
                        },
                        "cost".obj {
                            Cost.from(node).bind()
                        },
                        "weight".nullable {
                            "weight".double()
                        },
                        "url".str(),
                        "weapon_category".nullable {
                            "weapon_category".str()
                        },
                        "weapon_range".nullable {
                            "weapon_range".str()
                        },
                        "category_range".nullable {
                            "category_range".str()
                        },
                        "damage".nullable {
                            "damage".obj {
                                DamageDiceDamageBonusDamageType.from(node).bind()
                            }
                        },
                        "range".nullable {
                            "range".obj {
                                Range.from(node).bind()
                            }
                        },
                        "properties".nullable {
                            "properties".arr {
                                "properties".obj {
                                    NameUrl.from(node).bind()
                                }
                            }
                        },
                        "tool_category".nullable {
                            "tool_category".str()
                        },
                        "desc".nullable {
                            "desc".arr {
                                "desc".str()
                            }
                        },
                        "vehicle_category".nullable {
                            "vehicle_category".str()
                        },
                        "speed".nullable {
                            "speed".obj {
                                Cost.from(node).bind()
                            }
                        },
                        "2h_damage".nullable {
                            "2h_damage".obj {
                                DamageDiceDamageBonusDamageType.from(node).bind()
                            }
                        },
                        "contents".nullable {
                            "contents".arr {
                                "contents".obj {
                                    Contents.from(node).bind()
                                }
                            }
                        },
                        "capacity".nullable {
                            "capacity".str()
                        },
                        "armor_category".nullable {
                            "armor_category".str()
                        },
                        "armor_class".nullable {
                            "armor_class".obj {
                                ArmorClass.from(node).bind()
                            }
                        },
                        "str_minimum".nullable {
                            "str_minimum".int()
                        },
                        "stealth_disadvantage".nullable {
                            "stealth_disadvantage".boolean()
                        },
                        "quantity".nullable {
                            "quantity".int()
                        },
                        "throw_range".nullable {
                            "throw_range".obj {
                                ThrowRange.from(node).bind()
                            }
                        },
                        "special".nullable {
                            "special".arr {
                                "special".str()
                            }
                        }
                    )
                }
            }
        
    fun client(httpClient: HttpClient): ApiClient<CharacterEquipment> =
            Client(httpClient)
            
    protected class Client(override val httpClient: HttpClient): ApiClient<CharacterEquipment> {
        override val parse = parseResponseBody
        
        suspend fun getMyClass(index: String) = 
            getResourceByUri("/api/equipment/${index}")
    }

    }
}
