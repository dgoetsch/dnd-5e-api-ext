
package dandd.character.automation.models



data class ChooseTypeFrom(
    val choose: Int,
    val type: String,
    val from: List<NameUrl>
)
