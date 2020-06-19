
package dandd.character.automation.models



data class Trait(
    val _id: String,
    val index: String,
    val races: List<UrlName>,
    val subraces: List<UrlName>,
    val name: String,
    val desc: List<String>,
    val url: String
)
