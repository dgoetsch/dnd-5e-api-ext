
package dandd.character.automation.models



data class CharacterRace(
    val _id: String,
    val index: String,
    val name: String,
    val speed: Integer,
    val ability_bonuses: List<NameUrlBonus>,
    val alignment: String,
    val age: String,
    val size: String,
    val size_description: String,
    val starting_proficiencies: List<UrlName>,
    val languages: List<UrlName>,
    val language_desc: String,
    val traits: List<UrlName>,
    val subraces: List<UrlName>,
    val url: String,
    val starting_proficiency_options: ChooseTypeFrom?,
    val ability_bonus_options: AbilityBonusOptions?,
    val language_options: ChooseTypeFrom?,
    val trait_options: ChooseTypeFrom?
)
