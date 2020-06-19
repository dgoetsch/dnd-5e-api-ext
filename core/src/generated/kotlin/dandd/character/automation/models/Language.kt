
package dandd.character.automation.models



data class Language(
    val _id: String,
    val index: String,
    val name: String,
    val desc: String?,
    val type: String,
    val typical_speakers: List<String>,
    val script: String?,
    val url: String
)
