package components

import clients.Clients
import dandd.character.automation.models.classes.CharacterClass
import dandd.character.automation.models.races.CharacterRace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import player.PlayerCharacter
import react.*
import react.dom.div
import react.dom.h1
import react.dom.span
import web.core.mapRight


external interface PlayerCharacterState: RState {
    var characterRace: CharacterRace?
    var characterClass: CharacterClass?
}
external interface PlayerCharacterProps: RProps {
    var clients: Clients
    var coroutineScope: CoroutineScope
    var playerCharacter: PlayerCharacter
}

fun RBuilder.playerCharacter(handler: PlayerCharacterProps.() -> Unit): ReactElement {
    return child(PlayerCharacterComponent::class) {
        this.attrs(handler)
    }
}

class PlayerCharacterComponent(props: PlayerCharacterProps): RComponent<PlayerCharacterProps, PlayerCharacterState>(props) {
    override fun PlayerCharacterState.init(props: PlayerCharacterProps) {
        props.coroutineScope.launch {
            props.clients.races.getResource(props.playerCharacter.race)
                    .mapRight {model ->
                        setState {
                            characterRace = model
                        }
                    }

            props.playerCharacter.classLevels.forEach { (k, v) ->
                props.clients.classes.getResource(k)
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

            state.characterRace?.let { model ->
                characterRace {
                    characterRace = model
                }
            }
            state.characterClass?.let { model ->
                characterClass {
                    characterClass = model
                }
            }
        }
    }
}
