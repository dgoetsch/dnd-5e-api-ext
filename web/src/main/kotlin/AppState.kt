import clients.Clients
import kotlinx.coroutines.CoroutineScope
import player.PlayerCharacter
import player.PlayerService
import react.RState

external interface AppState: RState {
    var coroutineScope: CoroutineScope
    var clients: Clients
    var playerService: PlayerService
    var playerCharacters: List<PlayerCharacter>
}
