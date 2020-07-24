package dandd.character.automation
//
//import dandd.character.automation.models.CharacterSpell
//import kotlinx.html.*
//
//fun FlowContent.render(spell: CharacterSpell) {
//    div(classes = "card") {
//        h5(classes = "card-header") {
//            +spell.name
//            if(spell.ritual) + " (Ritual)"
//        }
//        div(classes = "card-body") {
//            h6(classes = "card-title") {
//                +"Level ${spell.level} ${spell.school.name}"
//            }
//            div(classes = "card-text") {
//                renderField("Casting Time", spell.casting_time)
//                renderField("Range", spell.range)
//                renderField("Components", spell.components.joinToString(", ")) {
//                    spell.material?.let { material -> span(classes = "font-italic") { +" ($material)" } }
//                }
//
//                val concentrationText = if(spell.concentration)
//                    "Concentration, ${spell.duration}"
//                else
//                    spell.duration
//
//                renderField("Duration", concentrationText)
//                spell.desc.forEach { p { +it } }
//                spell.higher_level?.let {higherLevels ->
//                    higherLevels.take(1).map {
//                        renderField("At higher levels", it) {
//                            higherLevels.drop(1).map { +" $it" }
//                        }
//                    }
//                }
//            }
//        }
//        /*
//        <div class="card-body">
//<h5 class="card-title">Special title treatment</h5>
//<p class="card-text">With supporting text below as a natural lead-in to additional content.</p>
//<a href="#" class="btn btn-primary">Go somewhere</a>
//</div>
//         */
//    }
//}