package dandd.character.automation.source

import dandd.character.automation.Result

data class ResourceConfig(
        val resourceName: String,
        val className: String,
        val subResources: List<ResourceConfig>,
        val readIndex: suspend (String) -> Result<String>)