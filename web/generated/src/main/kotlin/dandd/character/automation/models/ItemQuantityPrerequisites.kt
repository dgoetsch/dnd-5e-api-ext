
package dandd.character.automation.models



data class ItemQuantityPrerequisites(
    val item: NameUrl,
    val quantity: Int,
    val prerequisites: List<TypeProficiency>
)
