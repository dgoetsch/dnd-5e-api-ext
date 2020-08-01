import clients.Clients
import components.CharacterClassLevelComponent
import components.CharacterClassLevelProps
import kotlinx.coroutines.CoroutineScope
import player.PlayerCharacter
import player.PlayerService
import react.*
import kotlin.reflect.KClass

external interface AppState: RState, AppResources {
    var playerService: PlayerService
    var playerCharacters: List<PlayerCharacter>
}

external interface AppResources {
    var clients: Clients
    var coroutineScope: CoroutineScope
}

fun <T, C> RBuilder.appComponent(
        klass: KClass<C>,
        parent: AppResources,
        handler: T.() -> Unit
): ReactElement
        where T: RProps, T: AppResources, C: Component<T, *> {
    return child(klass) {
        attrs {
            copyFrom(parent)
            handler()
        }
    }
}



fun AppResources.copyFrom(other: AppResources) {
    clients = other.clients
    coroutineScope = other.coroutineScope
}