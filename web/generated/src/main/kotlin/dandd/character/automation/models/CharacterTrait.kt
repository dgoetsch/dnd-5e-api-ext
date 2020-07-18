
package dandd.character.automation.models



data class CharacterTrait(
    val _id: String,
    val index: String,
    val races: List<NameUrl>,
    val subraces: List<NameUrl>,
    val name: String,
    val desc: List<String>,
    val url: String
)
