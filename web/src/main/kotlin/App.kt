import dandd.character.automation.models.classes.CharacterClass
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import react.dom.h1
import react.dom.render
import kotlin.browser.document



fun main() {

    val client = HttpClient(Js) {}

    val mainScope = MainScope()
    mainScope.launch {
        val resource = CharacterClass.client(client).getResource("fighter")
        println(resource)
    }
    render(document.getElementById("root")) {
        h1 {
            +"Hello, React+Kotlin/JS!"

        }
    }
}