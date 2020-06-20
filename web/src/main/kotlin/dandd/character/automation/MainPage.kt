package dandd.character.automation

import dandd.character.automation.models.CharacterSpell
import kotlinx.html.*
import kotlinx.html.attributes.StringEncoder

data class MainPage(
        val characterName: String,
        val spells: List<CharacterSpell>) {

}


fun HTML.render(mainPage: MainPage) {
    head {
        link {
            rel="stylesheet"
            href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
            integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
            crossoriginAnonymous()
        }
        +"${mainPage.characterName} Character Info"
    }
    body {

        div(classes = "container-fluid") {
            div(classes = "row") {
                div(classes = "col-sm") { +"Col 1"}
                div(classes = "col-sm") { +"Col 2"}
                div(classes = "col-sm") { +"Col 3"}
            }

            div(classes = "row") {
                div(classes = "col") {
                    div(classes = "row") {
                        h2 {
                            "Spells"
                        }
                    }
                    mainPage.spells.forEach { spell ->
                        div {
                            div(classes = "row") {
                                div(classes = "col-xs") {

                                }
                                div(classes = "col") {
                                    render(spell)
                                }
                            }
                        }
                    }
                }
            }
        }
        script {
            src="https://code.jquery.com/jquery-3.3.1.slim.min.js"
            integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
            crossoriginAnonymous()
        }
        script {
            src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"
            integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1"
            crossoriginAnonymous()
        }
        script {
            src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"
            integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM"
            crossoriginAnonymous()
        }
    }
}

fun <F: FlowContent> F.renderField(name: String, value: String, additionalTags: P.() -> Unit = {}) {
    p {
        span(classes = "font-weight-bold") { +"$name: " }
        span { +value }
        additionalTags()
    }
}

private fun FlowContent.crossoriginAnonymous() {
    attributes.put("crossorigin", StringEncoder.encode("crossorigin", "anonymous"))
}