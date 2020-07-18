
package dandd.character.automation.models



data class CharacterAbilityScore(
    val _id: String,
    val index: String,
    val name: String,
    val full_name: String,
    val desc: List<String>,
    val skills: List<NameUrl>,
    val url: String
)
