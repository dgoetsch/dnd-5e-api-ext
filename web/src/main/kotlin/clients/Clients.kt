package clients

import dandd.character.automation.models.classes.CharacterClass
import dandd.character.automation.models.races.CharacterRace
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class Clients {
    private val client = HttpClient(Js) {}

    val mainScope = MainScope()

    val classes = CharacterClass.client(client)
    val races = CharacterRace.client(client)
}