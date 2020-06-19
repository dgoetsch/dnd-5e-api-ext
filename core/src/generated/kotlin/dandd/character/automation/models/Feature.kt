
package dandd.character.automation.models



data class Feature(
    val `class`: UrlName,
    val desc: List<String>,
    val _id: String,
    val index: String,
    val name: String,
    val level: Integer?,
    val url: String,
    val subclass: UrlName?,
    val prerequisites: List<Map<String, Any>>?,
    val group: String?,
    val choice: ChooseTypeFrom?,
    val reference: String?
)
