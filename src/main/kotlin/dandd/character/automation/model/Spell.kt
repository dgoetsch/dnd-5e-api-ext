package dandd.character.automation.model

data class Spell(val _id: String,
                 val index: String,
                 val url: String,

                 val name: String,
                 val ritual: Boolean,

                 val level: Int,
                 val school: ResourceReference,
                 val casting_time: String,
                 val range: String,
                 val components: List<String>,
                 val material: String?,

                 val desc: List<String>,
                 val higher_level: List<String>?,

                 val concentration: Boolean,
                 val duration: String,

                 val classes: List<ResourceReference>,
                 val subclasses: List<ResourceReference>
)