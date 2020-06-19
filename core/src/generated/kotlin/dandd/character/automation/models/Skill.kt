
package dandd.character.automation.models



data class Skill(
    val _id: String,
    val index: String,
    val name: String,
    val desc: List<String>,
    val ability_score: UrlName,
    val url: String
)
