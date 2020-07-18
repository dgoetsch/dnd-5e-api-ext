
package dandd.character.automation.models



data class CharacterSubclass(
    val _id: String,
    val index: String,
    val `class`: NameUrl,
    val name: String,
    val subclass_flavor: String,
    val desc: List<String>,
    val features: List<NameUrl>,
    val spells: List<PrerequisitesSpell>?,
    val url: String
)
