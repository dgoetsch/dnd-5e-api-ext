
package dandd.character.automation.models



data class CharacterRace(
    val _id: String,
    val index: String,
    val name: String,
    val speed: Int,
    val ability_bonuses: List<NameUrlBonus>,
    val alignment: String,
    val age: String,
    val size: String,
    val size_description: String,
    val starting_proficiencies: List<NameUrl>,
    val languages: List<NameUrl>,
    val language_desc: String,
    val traits: List<NameUrl>,
    val subraces: List<NameUrl>,
    val url: String,
    val starting_proficiency_options: ChooseTypeFrom?,
    val trait_options: ChooseTypeFrom?,
    val ability_bonus_options: AbilityBonusOptions?,
    val language_options: ChooseTypeFrom?
)
