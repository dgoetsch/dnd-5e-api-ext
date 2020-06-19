package dandd.character.automation

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dandd.character.automation.generator.KotlinClassWriter
import dandd.character.automation.generator.ModelRegistrar
import dandd.character.automation.generator.ModelTree
import dandd.character.automation.generator.Resources
import dandd.character.automation.source.createLoaderFor
import dandd.character.automation.source.suspendFlatMap
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking


fun main() {
    val objectMapper = jacksonObjectMapper()

    val targetDirectory = "src/main/kotlin"
    val resourcesBaseDirectory = "api-resources"
    val urlBase = "https://www.dnd5eapi.co"
    val pkg = "dandd.character.automation.models"


    val resources = Resources(objectMapper).of {
        listOf(
                resource("ability-scores", "CharacterAbilityScore"),
                resource("classes" ,  "CharacterClass"),
                resource("conditions" ,  "CharacterCondtion"),
                resource("damage-types" ,  "CharacterDamageType"),
                resource("equipment-categories" ,  "CharacterEquipmentCategory"),
                resource("equipment" ,  "CharacterEquipment"),
                resource("features" ,  "CharacterFeature", readName = readIndexField),
                resource("languages" ,  "CharacterLanguage"),
                resource("magic-schools" ,  "CharacterMagicSchool"),
                resource("proficiencies" ,  "CharacterProficiency"),
                resource("races" ,  "CharacterRace"),
                resource("skills" ,  "CharacterSkill"),
                resource("spellcasting" ,  "CharacterSpellcasting"),
                resource("spells" ,  "CharacterSpell"),
                resource("starting-equipment" ,  "CharacterStartingEquipment", readName = readIndexField),
                resource("subclasses" ,  "CharacterSubclass"),
                resource("subraces" ,  "CharacterSubrace"),
                resource("traits" ,  "CharacterTrait"),
                resource("weapon-properties" ,  "CharacterWeaponPropert"))
    }

    val registry = ModelRegistrar(pkg, resources)


    runBlocking {
        resources.map { (resourceName, className, getName, getId) -> async {
            val loader = createLoaderFor(
                    urlBase,
                    resourcesBaseDirectory,
                    resourceName,
                    { Either.Right(it) },
                    { Either.Right(it) },
                    getName,
                    getId
            )

            loader.loadAll()
                    .map {
                        async { it.suspendFlatMap { Either.catch {
                            val keyValues = it.toMap(objectMapper)
                            ModelTree(className, pkg, keyValues, registry)
                        } }
                        } }
                    .awaitAll()
        } }
                .awaitAll()
                .flatten()
                .forEach {
                    when(it) {
                        is Either.Left -> {
                            println("Encountered an error ${it.a}")
                            null
                        }
                        is Either.Right -> it.b
                    }
                }


    }

    KotlinClassWriter(pkg, registry.exportDictionary()).writeAll(targetDirectory)
}


fun String.toMap(mapper: ObjectMapper) =
        mapper
                .readValue(this, Map::class.java)
                .mapNotNull { entry -> entry.key?.let { key -> entry.value?. let { value -> when(key) {
                    is String -> key to value
                    else -> null
                } } }}
                .toMap()