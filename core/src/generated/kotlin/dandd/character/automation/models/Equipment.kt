
package dandd.character.automation.models



data class Equipment(
    val _id: String,
    val index: String,
    val name: String,
    val equipment_category: String,
    val gear_category: String?,
    val cost: Cost,
    val weight: Double?,
    val desc: List<String>?,
    val url: String,
    val vehicle_category: String?,
    val speed: Speed?,
    val tool_category: String?,
    val capacity: String?,
    val weapon_category: String?,
    val weapon_range: String?,
    val category_range: String?,
    val damage: DamageDiceDamageBonusDamageType?,
    val range: Map<String, Any>?,
    val properties: List<UrlName>?,
    val armor_category: String?,
    val armor_class: Map<String, Any>?,
    val str_minimum: Integer?,
    val stealth_disadvantage: Boolean?,
    val quantity: Integer?,
    val throw_range: ThrowRange?,
    val contents: List<ItemUrlQuantity>?,
    val special: List<String>?,
    val `2h_damage`: DamageDiceDamageBonusDamageType?
)
