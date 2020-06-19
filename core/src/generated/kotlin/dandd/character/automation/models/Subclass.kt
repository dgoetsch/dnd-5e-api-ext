
package dandd.character.automation.models



data class Subclass(
    val _id: String,
    val index: String,
    val `class`: UrlName,
    val name: String,
    val subclass_flavor: String,
    val desc: List<String>,
    val features: List<UrlName>,
    val url: String,
    val spells: List<PrerequisitesSpell>?
)
