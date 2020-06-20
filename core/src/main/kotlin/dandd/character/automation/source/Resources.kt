package dandd.character.automation.source

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import dandd.character.automation.Result
import dandd.character.automation.toMap

class Resources(
        val objectMapper: ObjectMapper
) {

    val readIndexField: suspend (String) -> Result<String> =
            { json: String ->
                Either.catch {
                    val index = json.toMap(objectMapper).get("index")
                    when (index) {
                        null -> throw java.lang.RuntimeException(" No INdex for $json")
                        else -> index.toString()
                    }
                }
            }

    fun of(body: Resources.() -> List<ResourceConfig>): List<ResourceConfig> = body()

    fun resource(
            name: String,
            className: String,
            readIndex: suspend (String) -> Result<String> = readIndexField
    ) = ResourceConfig(name, className, readIndex)

    fun dAndDResources() = of {
        listOf(
                resource("ability-scores", "CharacterAbilityScore"),
                resource("classes" ,  "CharacterClass"),
                resource("conditions" ,  "CharacterCondtion"),
                resource("damage-types" ,  "CharacterDamageType"),
                resource("equipment-categories" ,  "CharacterEquipmentCategory"),
                resource("equipment" ,  "CharacterEquipment"),
                resource("features" ,  "CharacterFeature"),
                resource("languages" ,  "CharacterLanguage"),
                resource("magic-schools" ,  "CharacterMagicSchool"),
                resource("proficiencies" ,  "CharacterProficiency"),
                resource("races" ,  "CharacterRace"),
                resource("skills" ,  "CharacterSkill"),
                resource("spellcasting" ,  "CharacterSpellcasting"),
                resource("spells" ,  "CharacterSpell"),
                resource("starting-equipment" ,  "CharacterStartingEquipment"),
                resource("subclasses" ,  "CharacterSubclass"),
                resource("subraces" ,  "CharacterSubrace"),
                resource("traits" ,  "CharacterTrait"),
                resource("weapon-properties" ,  "CharacterWeaponPropert"))
    }
}