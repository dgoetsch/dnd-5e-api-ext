package components

import AppResources
import copyFrom
import dandd.character.automation.models.classes.CharacterClass
import dandd.character.automation.models.races.CharacterRace
import kotlinx.coroutines.launch
import player.PlayerCharacter
import react.*
import react.dom.div
import react.dom.h1
import react.dom.span
import web.core.mapLeft
import web.core.mapRight

external interface PlayerCharacterState: RState {
    var characterRace: CharacterRace?
    var characterClass: CharacterClass?
}
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
            props.clients.races.getCharacterRace(props.playerCharacter.race)
                    .mapRight { model ->
                        setState {
                            characterRace = model
                        }
                    }
                    .mapLeft { println("error: $it") }

            props.playerCharacter.classLevels.forEach { (k, v) ->
                props.clients.classes.getCharacterClass(k)
                        .mapRight { model ->
                            setState {
                                characterClass = model
                            }
                        }
            }
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

            div("row") {
                div("col") {
                    state.characterRace?.let { model ->
                        characterRace(props) {
                            characterRace = model
                        }
                    }
                }
                div("col") {
                    state.characterClass?.let { model ->
                        characterClass(props) {
                            characterClass = model
                        }
                    }
                }
            }
        }
    }
}
