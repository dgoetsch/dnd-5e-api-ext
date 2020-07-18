
package dandd.character.automation.models



data class CharacterFeature(
    val `class`: NameUrl,
    val desc: List<String>,
    val _id: String,
    val index: String,
    val name: String,
    val level: Int?,
    val url: String,
    val subclass: NameUrl?,
    val prerequisites: List<Map<String, Any>>?,
    val group: String?,
    val choice: ChooseTypeFrom?,
    val reference: String?
)
