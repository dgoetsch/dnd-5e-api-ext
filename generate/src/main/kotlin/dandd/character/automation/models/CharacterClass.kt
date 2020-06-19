
package dandd.character.automation.models



data class CharacterClass(
    val _id: String,
    val index: String,
    val name: String,
    val hit_die: Integer,
    val proficiency_choices: List<ChooseTypeFrom>,
    val proficiencies: List<UrlName>,
    val saving_throws: List<UrlName>,
    val starting_equipment: UrlClass,
    val class_levels: UrlClass,
    val subclasses: List<UrlName>,
    val url: String,
    val spellcasting: UrlClass?
)
