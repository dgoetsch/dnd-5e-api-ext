import clients.Clients
import kotlinx.coroutines.CoroutineScope
import player.PlayerCharacter
import player.PlayerService
import react.RState

external interface AppState: RState, AppResources {
    var playerService: PlayerService
    var playerCharacters: List<PlayerCharacter>
}

external interface AppResources {
    var clients: Clients
    var coroutineScope: CoroutineScope
}

fun AppResources.copyFrom(other: AppResources) {
    clients = other.clients
    coroutineScope = other.coroutineScope
}