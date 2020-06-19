
package dandd.character.automation.models



data class Proficiency(
    val _id: String,
    val index: String,
    val type: String,
    val name: String,
    val classes: List<UrlName>,
    val races: List<UrlName>,
    val url: String
)
