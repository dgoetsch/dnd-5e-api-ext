package dandd.character.automation.source

import dandd.character.automation.Result

internal interface ResourcePersister<T> {
    suspend fun persistResource(name: String, resource: T): Result<T>
}