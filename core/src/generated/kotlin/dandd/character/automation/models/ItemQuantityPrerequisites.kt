
package dandd.character.automation.models



data class ItemQuantityPrerequisites(
    val item: UrlName,
    val quantity: Integer,
    val prerequisites: List<TypeProficiency>
)
