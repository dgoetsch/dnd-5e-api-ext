package dandd.character.automation.model

data class Spell(val _id: String,
                 val index: String,
                 val url: String,

                 val name: String,
                 val level: Int,
                 val desc: List<String>,
                 val higher_level: List<String>?,

                 val components: List<String>,
                 val material: String?,
                 val concentration: Boolean,

                 val range: String,
                 val casting_time: String,
                 val duration: String,
                 val ritual: Boolean,

                 val school: ResourceReference,
                 val classes: List<ResourceReference>,
                 val subclasses: List<ResourceReference>
)