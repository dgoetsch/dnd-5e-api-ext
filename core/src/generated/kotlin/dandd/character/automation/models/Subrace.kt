
package dandd.character.automation.models



data class Subrace(
    val _id: String,
    val index: String,
    val name: String,
    val race: UrlName,
    val desc: String,
    val ability_bonuses: List<NameUrlBonus>,
    val starting_proficiencies: List<UrlName>,
    val languages: List<UrlName>,
    val racial_traits: List<UrlName>,
    val url: String,
    val language_options: ChooseTypeFrom?,
    val racial_trait_options: ChooseTypeFrom?
)
