
package dandd.character.automation.models



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
    val range: Map<String, Any>?,
    val properties: List<NameUrl>?,
    val tool_category: String?,
    val desc: List<String>?,
    val vehicle_category: String?,
    val speed: Cost?,
//    val 2h_damage: DamageDiceDamageBonusDamageType?,
    val contents: List<ItemUrlQuantity>?,
    val capacity: String?,
    val armor_category: String?,
    val armor_class: Map<String, Any>?,
    val str_minimum: Int?,
    val stealth_disadvantage: Boolean?,
    val quantity: Int?,
    val throw_range: ThrowRange?,
    val special: List<String>?
)
