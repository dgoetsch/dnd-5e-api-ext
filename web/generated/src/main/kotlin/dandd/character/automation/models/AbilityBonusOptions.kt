
package dandd.character.automation.models

import kotlin.js.Json

data class Thing(private val json: Json) {
    fun thing(): Int {
        return json.get("thing").unsafeCast<Int>()
    }


}

data class Thing2(private val json: dynamic) {
    fun _2h(): Boolean {
        return json["2h"] as Boolean
    }
}

data class AbilityBonusOptions(
    val choose: Int,
    val type: String,
    val from: List<NameUrlBonus>
)
