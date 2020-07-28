package components

import AppResources
import copyFrom
import dandd.character.automation.models.proficiencies.CharacterProficiency
import react.*
import react.dom.*


external interface CharacterProficiencyProps: RProps, AppResources {
    var characterProficiency: CharacterProficiency
}

external interface CharacterProficiencyState: RState

fun RBuilder.characterProficiency(parent: AppResources, handler: CharacterProficiencyProps.() -> Unit): ReactElement {
    return child(CharacterProficiencyComponent::class) {
        attrs {
            copyFrom(parent)
            handler()
        }
    }
}

class CharacterProficiencyComponent(props: CharacterProficiencyProps): RComponent<CharacterProficiencyProps, CharacterProficiencyState>(props) {
    override fun CharacterProficiencyState.init(props: CharacterProficiencyProps) {

    }

    override fun RBuilder.render() {
        h4 {
            span { strong { +"Proficiency: "  } }
            span { +props.characterProficiency.name }
        }
        div {
            div {
                span { strong { +"Type "  } }
                span { +props.characterProficiency.type }
            }
            div {
                span { strong { +"Url "  } }
                span { +props.characterProficiency.url }
            }
        }
    }
}

