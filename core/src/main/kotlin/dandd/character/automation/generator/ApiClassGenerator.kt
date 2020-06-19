package dandd.character.automation.generator

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dandd.character.automation.source.createLoaderFor
import dandd.character.automation.source.suspendFlatMap
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.lang.RuntimeException


fun main() {
    val objectMapper = jacksonObjectMapper()

    val targetDirectory = "src/generated/kotlin"
    val resourcesBaseDirectory = "src/main/resources/api"
    val urlBase = "https://www.dnd5eapi.co"
    val pkg = "dandd.character.automation.models"

    val registry =  ModelRegistrar(pkg)


    val resources = Resources(objectMapper).of {
        listOf(
                resource("ability-scores", "AbilityScore"),
                resource("classes" ,  "Class"),
                resource("conditions" ,  "Condtion"),
                resource("damage-types" ,  "DamageType"),
                resource("equipment-categories" ,  "EquipmentCategory"),
                resource("equipment" ,  "Equipment"),
                resource("features" ,  "Feature", readName = readIndexField),
                resource("languages" ,  "Language"),
                resource("magic-schools" ,  "MagicSchool"),
                resource("proficiencies" ,  "Proficiency"),
                resource("races" ,  "Race"),
                resource("skills" ,  "Skill"),
                resource("spellcasting" ,  "Spellcasting"),
                resource("spells" ,  "Spell"),
                resource("starting-equipment" ,  "StartingEquipment", readName = readIndexField),
                resource("subclasses" ,  "Subclass"),
                resource("subraces" ,  "Subrace"),
                resource("traits" ,  "Trait"),
                resource("weapon-properties" ,  "WeaponPropert"))
    }

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


