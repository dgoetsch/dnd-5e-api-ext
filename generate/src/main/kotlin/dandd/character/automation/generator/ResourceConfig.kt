package dandd.character.automation.generator

import dandd.character.automation.Result

data class ResourceConfig(
        val resourceName: String,
        val className: String,
        val readName: suspend (String) -> Result<String>,
        val readIndex: suspend (String) -> Result<String>)