
package dandd.character.automation.models



data class PrerequisitesSpell(
    val prerequisites: List<Map<String, Any>>,
    val spell: UrlName
)
