package dandd.character.automation.source


data class ResourceConfig<T, ID: Any>(
        val resourceName: String,
        val className: String,
        val resourceLoader: ResourceLoader<T, ID>)