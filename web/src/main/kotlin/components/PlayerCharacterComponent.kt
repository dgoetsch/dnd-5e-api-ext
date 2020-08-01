package components

import AppResources
import copyFrom
import dandd.character.automation.models.classes.CharacterClass
import dandd.character.automation.models.classes.ProficiencyChoices
import dandd.character.automation.models.classes.levels.CharacterClassLevel
import dandd.character.automation.models.features.CharacterFeature
import dandd.character.automation.models.proficiencies.CharacterProficiency
import dandd.character.automation.models.races.CharacterRace
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import player.PlayerCharacter
import react.*
import react.dom.*
import web.api.ApiClient
import web.core.liftRight
import web.core.mapLeft
import web.core.mapRight
import web.core.suspendBindRight

external interface PlayerCharacterState: RState {
    var characterRace: CharacterRace?
    var characterClasses: Map<CharacterClass, HydradetCharacterClass>?
}

data class HydradetCharacterClass(val levels: Map<CharacterClassLevel, HydratedCharacterClassLevel>,
                                  val proficiencies: List<CharacterProficiency>,
                                  val proficiencyChoices: Map<String, CharacterProficiency>)


data class HydratedCharacterClassLevel(
        val features: List<CharacterFeature>,
        val featureChoices: List<CharacterFeature>
)
external interface PlayerCharacterProps: RProps, AppResources {
    var playerCharacter: PlayerCharacter
}

fun RBuilder.playerCharacter(appResources: AppResources, handler: PlayerCharacterProps.() -> Unit): ReactElement {
    return child(PlayerCharacterComponent::class) {
        this.attrs {
            copyFrom(appResources)
            handler()
        }
    }

}

class PlayerCharacterComponent(props: PlayerCharacterProps): RComponent<PlayerCharacterProps, PlayerCharacterState>(props) {
    override fun PlayerCharacterState.init(props: PlayerCharacterProps) {
        props.coroutineScope.launch {
            ApiClient {
                val race = props.clients.races.getCharacterRace(props.playerCharacter.race).bind()

                val classes =  props.playerCharacter.classLevels
                        .map { (k, v) -> async {
                            val playerCLass = props.clients.classes.getCharacterClass(k).bind()

                            val classLevelsJ = (1..v).map { async {
                                val classLevel = props.clients.classLevels.getCharacterClassLevel(playerCLass.index, it.toString()).bind()
                                val classLevelFeaturesJ = classLevel.features.map { async {
                                    props.clients.features.getResourceByUri(it.url).bind()
                                } }
                                val classLevelFeatureChoicesJ = classLevel.feature_choices.map { async {
                                    props.clients.features.getResourceByUri(it.url).bind()
                                } }

                                classLevel to HydratedCharacterClassLevel(
                                        classLevelFeaturesJ.awaitAll(),
                                        classLevelFeatureChoicesJ.awaitAll())
                            } }

                            val proficienciesJ = playerCLass.proficiencies.map { async {
                                props.clients.proficienies.getResourceByUri(it.url).bind()
                            } }

                            val proficiencyChoiceJ = playerCLass.proficiency_choices
                                    .flatMap { it.from }
                                    .map { async { it.url to props.clients.proficienies.getResourceByUri(it.url).bind() } }

                            playerCLass to HydradetCharacterClass(
                                    classLevelsJ.awaitAll().toMap(),
                                    proficienciesJ.awaitAll(),
                                    proficiencyChoiceJ.awaitAll().toMap())
                        } }
                        .awaitAll()
                        .toMap()

                setState {
                    characterRace = race
                    characterClasses = classes
                }
            }.mapLeft { println(it) }
        }
    }

    override fun RBuilder.render() {

        h1 {
            +"Player Character"
        }

        div {
            span {
              +"HP:"
            }
            span {
                +props.playerCharacter.currentHitPoints.toString()
            }
            span { +" / "}

            span {
                +props.playerCharacter.maxHitPoints.toString()
            }

            div {
                state.characterRace?.let { model ->
                    characterRace(props) {
                        characterRace = model
                    }
                }
            }
            state.characterClasses?.forEach { (model, hydrated) ->

                div {
                    characterClass(props) {
                        characterClass = model
                    }
                }
                div {
                    characterClassLevel(props) {
                        characterClassLevel = hydrated.levels.keys.toList()
                        currentLevel = hydrated.levels.size
                    }
                }

                if(hydrated.levels.any { it.value.features.isNotEmpty() }) {
                    h3 { +"Features" }
                    hydrated.levels.forEach { (classLevel, features) ->
                        if (features.features.isNotEmpty()) {
                            h4 { +"Level ${classLevel.level}" }
                            features.features.forEach {
                                characterFeature(props) {
                                    feature = it
                                }
                            }
                        }
                    }
                }
                if(hydrated.levels.any { it.value.featureChoices.isNotEmpty() }) {
                    h3 { +"Features Choices" }
                    hydrated.levels.forEach { (classLevel, features) ->
                        if (features.featureChoices.isNotEmpty()) {
                            h4 { +"Level ${classLevel.level}" }
                            features.featureChoices.forEach {
                                characterFeature(props) {
                                    feature = it
                                }
                            }
                        }
                    }
                }

                hydrated.proficiencies.map {
                    characterProficiency(props) {
                        characterProficiency = it
                    }
                }

                model.proficiency_choices.map {
                    div {
                        div {
                            span { strong { +"Choose " } }
                            span { +it.choose.toString() }
                            span { +" From" }
                        }

                        it.from.map {
                            hydrated.proficiencyChoices.get(it.url)?.let {
                                div("row") {
                                    div("col-2 col-sm-1") {

                                    }
                                    div("col-10 col-sm-11") {
                                        characterProficiency(props) {
                                            characterProficiency = it
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                hydrated.proficiencyChoices
            }
        }
    }
}
