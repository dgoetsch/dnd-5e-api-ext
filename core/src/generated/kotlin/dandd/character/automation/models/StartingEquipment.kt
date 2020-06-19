
package dandd.character.automation.models



data class StartingEquipment(
    val `class`: UrlName,
    val _id: String,
    val index: Integer,
    val starting_equipment: List<ItemQuantity>,
    val choices_to_make: Integer,
    val choice_1: List<Map<String, Any>>,
    val choice_2: List<Map<String, Any>>,
    val choice_3: List<Map<String, Any>>?,
    val choice_4: List<Map<String, Any>>?,
    val url: String,
    val choice_5: List<Map<String, Any>>?
)
