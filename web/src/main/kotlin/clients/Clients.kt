package clients

import dandd.character.automation.models.classes.CharacterClass
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class Clients {
    val client = HttpClient(Js) {}

    val mainScope = MainScope()

    fun getResource() {
        mainScope.launch {
            val resource = CharacterClass.client(client).getResource("fighter")
            println(resource)
        }
    }
}