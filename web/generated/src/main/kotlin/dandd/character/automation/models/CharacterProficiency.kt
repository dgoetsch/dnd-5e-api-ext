
package dandd.character.automation.models



data class CharacterProficiency(
    val _id: String,
    val index: String,
    val type: String,
    val name: String,
    val classes: List<NameUrl>,
    val races: List<NameUrl>,
    val url: String
)
