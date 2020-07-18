
package dandd.character.automation.models



data class CharacterSkill(
    val _id: String,
    val index: String,
    val name: String,
    val desc: List<String>,
    val ability_score: NameUrl,
    val url: String
)
