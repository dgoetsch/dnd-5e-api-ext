
package dandd.character.automation.models



data class CharacterSubrace(
    val _id: String,
    val index: String,
    val name: String,
    val race: NameUrl,
    val desc: String,
    val ability_bonuses: List<NameUrlBonus>,
    val starting_proficiencies: List<NameUrl>,
    val languages: List<NameUrl>,
    val language_options: ChooseTypeFrom?,
    val racial_traits: List<NameUrl>,
    val racial_trait_options: ChooseTypeFrom?,
    val url: String
)
