
package dandd.character.automation.models



data class CharacterSpell(
    val _id: String,
    val index: String,
    val name: String,
    val desc: List<String>,
    val range: String,
    val components: List<String>,
    val material: String?,
    val ritual: Boolean,
    val duration: String,
    val concentration: Boolean,
    val casting_time: String,
    val level: Int,
    val school: NameUrl,
    val classes: List<NameUrl>,
    val subclasses: List<NameUrl>,
    val url: String,
    val higher_level: List<String>?
)
