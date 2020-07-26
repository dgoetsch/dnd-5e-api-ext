package components

import dandd.character.automation.models.classes.CharacterClass
import react.*
import react.dom.*


external interface CharacterClassProps: RProps {
    var characterClass: CharacterClass
}

fun RBuilder.characterClass(handler: CharacterClassProps.() -> Unit): ReactElement {
    return child(CharacterClassComponent::class) {
        this.attrs(handler)
    }
}

class CharacterClassComponent: RComponent<CharacterClassProps, RState>() {
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
                }
            }
        }
    }
}