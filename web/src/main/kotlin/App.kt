import clients.Clients
import components.playerCharacter
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import player.StaticPlayerService
import react.*
import react.dom.h1
import react.dom.render
import kotlin.browser.document
import kotlin.browser.window

fun main() {
    window.onload = {
        document.body!!.insertAdjacentHTML("afterbegin", "<div class='container' id='root'></div>")
        render(document.getElementById("root")) {
            child(App::class) {

            }
        }
    }
}

class App: RComponent<RProps, AppState>() {
    override fun AppState.init() {
        coroutineScope = MainScope()
        clients = Clients()
        playerService = StaticPlayerService()
        playerCharacters = emptyList()
        coroutineScope.launch {
            val pCs = state.playerService.getCharacters()
            setState {
                playerCharacters = pCs
            }
        }
    }

    override fun RBuilder.render() {
        h1 {
            +"Hello, React+Kotlin/JS!"
        }

        state.playerCharacters.forEach { model ->
            playerCharacter(state) {
                playerCharacter = model
            }
        }
    }
}
