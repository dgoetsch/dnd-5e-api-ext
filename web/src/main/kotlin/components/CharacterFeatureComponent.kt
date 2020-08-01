package components

import AppResources
import appComponent
import dandd.character.automation.models.features.CharacterFeature
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.html.ButtonType
import kotlinx.html.Tag
import kotlinx.html.id
import react.*
import react.dom.*
import web.core.liftRight
import web.core.mapRight
import kotlin.browser.document
import kotlin.dom.addClass

import kotlinx.html.js.onClickFunction

external interface CharacterFeatureProps: RProps, AppResources {
    var feature: CharacterFeature
}

external interface CharacterFeatureState: RState {
    var choices: List<CharacterFeature>?
}

fun RBuilder.characterFeature(parent: AppResources, handler: CharacterFeatureProps.() -> Unit): ReactElement =
        appComponent(CharacterFeatureComponent::class, parent, handler)

class CharacterFeatureComponent(props: CharacterFeatureProps): RComponent<CharacterFeatureProps, CharacterFeatureState>(props) {
    override fun CharacterFeatureState.init(props: CharacterFeatureProps) {
        props.feature.choice?.let {
            props.coroutineScope.launch { it.from
                    .map { async {
                        props.clients.features.getResourceByUri(it.url)
                    } }
                    .awaitAll()
                    .liftRight()
                    .mapRight {
                        setState {
                            choices = it
                        }
                    }
            }
        }
    }
    override fun RBuilder.render() {
        div {
            div { strong { +props.feature.name } }
            props.feature.group?.let { group ->
                div {
                    span { strong { +"Group " } }
                    span { +group }
                }
            }
            props.feature.prerequisites?.let {
                div { strong { +"Prerequisites" } }
                it.map {
                    it.type.let { label("Type", it)}
                    it.level?.let { label("Level", it.toString()) }
                    it.feature?.let { label("Feature", it) }
                    it.proficiency?.let { label("Proficiency", it)}
                    it.spell?.let { label("Spell", it) }
                }
            }

            props.feature.desc.map {
                div { +it }
            }

            state.choices?.let { choices ->
                val elementId = "${props.feature._id}-choices"
                collapsable(elementId) {
                    div("card card-body") {
                        choices.map {
                            div("row") {
                                div("col-2 col-sm-1") {

                                }
                                div("col-10 col-sm-11") {
                                    characterFeature(props) {
                                        feature = it
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun <E: Tag> RDOMBuilder<E>.label(name: String, value: String) {
    div {
        span { strong { +name } }
        span { +value }
    }
}

fun RBuilder.collapsable(
        elementId: String,
        body: RBuilder.() -> Unit
) {
    div {
        var isVisible = false
        div {
            button(classes = "btn btn-primary",
                    type = ButtonType.button) {
                attrs {
                    id = "$elementId-button"
                    onClickFunction = {
                        isVisible = !isVisible
                        val clazz = if (isVisible) "show" else "collapse"
                        document.getElementById(elementId)
                                ?.className = clazz
                        document.getElementById("$elementId-button")
                                ?.innerHTML =  if(isVisible) "-" else "+"
                    }
                }
                +"+"
            }
        }
        div("collapse") {
            attrs {
                id = elementId
            }
            body()
        }
    }
}