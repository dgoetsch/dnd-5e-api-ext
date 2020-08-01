package clients

import dandd.character.automation.models.classes.CharacterClass
import dandd.character.automation.models.classes.levels.CharacterClassLevel
import dandd.character.automation.models.equipment.CharacterEquipment
import dandd.character.automation.models.equipment.categories.CharacterEquipmentCategory
import dandd.character.automation.models.features.CharacterFeature
import dandd.character.automation.models.proficiencies.CharacterProficiency
import dandd.character.automation.models.races.CharacterRace
import dandd.character.automation.models.skills.CharacterSkill
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import react.RBuilder

class Clients {
    private val client = HttpClient(Js) {}

    val classes = CharacterClass.client(client)
    val classLevels = CharacterClassLevel.client(client)
    val races = CharacterRace.client(client)
    val proficienies = CharacterProficiency.client(client)
    val features = CharacterFeature.client(client)
    val equipment = CharacterEquipment.client(client)
    val equipmentCategory = CharacterEquipmentCategory.client(client)
    val skills = CharacterSkill.client(client)
    val abilityScore = CharacterSkill.client(client)


    fun RBuilder.display(url: String) {
        url.split("/")

    }
}