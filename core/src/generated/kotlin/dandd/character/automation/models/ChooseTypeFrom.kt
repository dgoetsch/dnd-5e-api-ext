
package dandd.character.automation.models



data class ChooseTypeFrom(
    val choose: Integer,
    val type: String,
    val from: List<UrlName>
)
