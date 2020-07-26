package components

import dandd.character.automation.models.races.CharacterRace
import react.*
import react.dom.div
import react.dom.h2
import react.dom.span

external interface CharacterRaceProps: RProps {
    var characterRace: CharacterRace
}

fun RBuilder.characterRace(handler: CharacterRaceProps.() -> Unit): ReactElement {
    return child(CharacterRaceComponent::class) {
        this.attrs(handler)
    }
}

class CharacterRaceComponent: RComponent<CharacterRaceProps, RState>() {
    override fun RBuilder.render() {

        h2 {
            +"Race"
        }
        div {
            span {
               +"name: "
            }

            span {
                +props.characterRace.name
            }
        }
    }
}