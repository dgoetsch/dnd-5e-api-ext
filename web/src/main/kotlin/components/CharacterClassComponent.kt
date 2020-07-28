package components

import AppResources
import copyFrom
import dandd.character.automation.models.classes.CharacterClass
import dandd.character.automation.models.proficiencies.CharacterProficiency
import kotlinx.coroutines.launch
import react.*
import react.dom.*
import web.core.bindRight
import web.core.liftRight
import web.core.mapRight


external interface CharacterClassProps: RProps, AppResources {
    var characterClass: CharacterClass
}

external interface CharacterClassState: RState {
    var proficiencies: List<CharacterProficiency>?
}

fun RBuilder.characterClass(parent: AppResources, handler: CharacterClassProps.() -> Unit): ReactElement {
    return child(CharacterClassComponent::class) {
        attrs {
            copyFrom(parent)
            handler()
        }
    }
}

class CharacterClassComponent(props: CharacterClassProps): RComponent<CharacterClassProps, CharacterClassState>(props) {
    override fun CharacterClassState.init(props: CharacterClassProps) {
        props.coroutineScope.launch {
            props.characterClass.proficiencies
                    .map { props.clients.proficienies.getResourceByUri(it.url) }
                    .liftRight()
                    .mapRight {
                        setState {
                            proficiencies = it
                        }
                    }
        }

    }

    override fun RBuilder.render() {
        div("card") {
            div("card-body") {
                h4("card-title") {
                    span { strong { +"Class: "  } }
                    span { +props.characterClass.name }
                }
                div("card-text") {
                    div {
                        span { strong { +"Hit Die "  } }
                        span { +props.characterClass.hit_die.toString() }
                    }
                    state.proficiencies?.map {
                        characterProficiency(props) {
                            characterProficiency = it
                        }
                    }
                }
            }
        }
    }
}