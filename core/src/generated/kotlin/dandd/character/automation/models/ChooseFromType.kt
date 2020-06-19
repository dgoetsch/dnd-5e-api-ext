
package dandd.character.automation.models



data class ChooseFromType(
    val choose: Integer,
    val from: List<UrlName>,
    val type: String
)
