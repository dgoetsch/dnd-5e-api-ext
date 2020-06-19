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

private fun FlowContent.render(spell: CharacterSpell) {
    div(classes = "card") {
        h5(classes = "card-header") {
            +spell.name
            if(spell.ritual) + " (Ritual)"
        }
        div(classes = "card-body") {
            h6(classes = "card-title") {
                +"Level ${spell.level} ${spell.school.name}"
            }
            div(classes = "card-text") {
                p {
                    renderField("Casting Time", spell.casting_time)
                }
                p {
                    renderField("Range", spell.range)
                }
                p {
                    renderField("Components", spell.components.joinToString(", "))
                    spell.material?.let { material -> span(classes = "font-italic" ) { +" ($material)" }}
                }
                p {
                    val concentrationText = if(spell.concentration)
                        "Concentration, ${spell.duration}"
                    else
                        spell.duration

                    renderField("Duration", concentrationText)
                }
                spell.desc.forEach { p { +it } }
                spell.higher_level?.let {
                    it.take(1).map {
                        p { renderField("At higher levels", it) }
                    }
                    it.drop(1).map { p { +it} }
                }
            }
        }
        /*
        <div class="card-body">
<h5 class="card-title">Special title treatment</h5>
<p class="card-text">With supporting text below as a natural lead-in to additional content.</p>
<a href="#" class="btn btn-primary">Go somewhere</a>
</div>
         */
    }
}

fun FlowContent.renderField(name: String, value: String) {
    span(classes = "font-weight-bold") { +"$name: " }
    span { +value }
}
private fun FlowContent.crossoriginAnonymous() {
    attributes.put("crossorigin", StringEncoder.encode("crossorigin", "anonymous"))
}