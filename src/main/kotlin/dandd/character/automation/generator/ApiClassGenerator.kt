package dandd.character.automation.generator

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dandd.character.automation.source.createLoaderFor
import dandd.character.automation.source.suspendFlatMap
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking


fun main() {
    val objectMapper = jacksonObjectMapper()

    val targetDirectory = "src/generated/kotlin"
    val resourcesBaseDirectory = "src/main/resources/api"
    val urlBase = "https://www.dnd5eapi.co"
    val pkg = "dandd.character.automation.models"

    val registry =  ModelRegistrar(pkg)

    val resources = listOf(
        "ability-scores" to "AbilityScore",
        "classes" to "Class",
        "conditions" to "Condtion",
        "damage-types" to "DamageType",
        "equipment-categories" to "EquipmentCategory",
        "equipment" to "Equipment",
        "features" to "Feature",
        "languages" to "Language",
        "magic-schools" to "MagicSchool",
//        "monsters" to "Monster",
        "proficiencies" to "Proficiency",
        "races" to "Race",
        "skills" to "Skill",
        "spellcasting" to "Spellcasting",
        "spells" to "Spell",
        "starting-equipment" to "StartingEquipment",
        "subclasses" to "Subclass",
        "subraces" to "Subrace",
        "traits" to "Trait",
        "weapon-properties" to "WeaponPropert")

    runBlocking {
        resources.map { (resourceName, className) -> async {
            val loader = createLoaderFor(
                    urlBase,
                    resourcesBaseDirectory,
                    resourceName,
                    { Either.Right(it) },
                    { Either.Right(it) },
                    {
                        Either.catch {
                            val name = it.toMap(objectMapper).get("name")
                            when (name) {
                                null -> throw RuntimeException("name not found in $it")
                                else -> name.toString()
                            }
                        }
                    },
                    {
                        Either.catch {
                            val index = it.toMap(objectMapper).get("index")
                            when (index) {
                                is String -> index
                                else -> throw java.lang.RuntimeException("No INdex for $it")
                            }
                        }
                    }
            )

            loader.loadAll()
                    .map {
                        async { it.suspendFlatMap { Either.catch {
                            val keyValues = it.toMap(objectMapper)
                            ModelTree(className, pkg, keyValues, registry)
                        } }
                    } }
                    .awaitAll()
            }
        }
                .awaitAll()
                .flatten()
                .forEach {
                    when(it) {
                        is Either.Left -> {
                            println("Encountered an error ${it.a}")
                            it.a.printStackTrace()
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


