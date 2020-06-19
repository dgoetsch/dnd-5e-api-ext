
package dandd.character.automation.models



data class EquipmentCategory(
    val _id: String,
    val index: String,
    val name: String,
    val equipment: List<UrlName>,
    val url: String
)
