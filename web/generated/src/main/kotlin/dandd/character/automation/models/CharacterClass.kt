
package dandd.character.automation.models



data class CharacterClass(
    val _id: String,
    val index: String,
    val name: String,
    val hit_die: Int,
    val proficiency_choices: List<ChooseTypeFrom>,
    val proficiencies: List<NameUrl>,
    val saving_throws: List<NameUrl>,
    val starting_equipment: UrlClass,
    val class_levels: UrlClass,
    val subclasses: List<NameUrl>,
    val url: String,
    val spellcasting: UrlClass?
)
