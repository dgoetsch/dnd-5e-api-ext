package components

import dandd.character.automation.models.classes.CharacterClass
import dandd.character.automation.models.races.CharacterRace
import react.*
import react.dom.div
import react.dom.h2
import react.dom.span


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
        h2 {
            +"Class"
        }
        div {
            span {
                +"name: "
            }

            span {
                +props.characterClass.name
            }
        }
    }
}