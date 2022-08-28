package wasteservice.state

import com.google.gson.JsonObject
import com.google.gson.JsonParser

enum class Material {
    GLASS, PLASTIC
}

data class WasteState(
    private val currentLoads : MutableMap<Material, Double> = Material.values()
        .associateWith { 0.0 }
        .toMutableMap()
) {

    companion object {
        @JvmStatic
        fun fromStringRepresentation(string : String) : WasteState {
            val jsonObject = JsonParser.parseString(string).asJsonObject
            val parsedLoads = mutableMapOf<Material, Double>()
            Material.values().forEach {
                if(jsonObject.has(it.toString()))
                    parsedLoads[it] = jsonObject[it.toString()].asDouble
            }

            return WasteState(parsedLoads)
        }

    }

    fun getLoadOf(material: Material) : Double {
        return currentLoads[material]!!
    }

    fun getLoadOf(material: String) : Double {
        return getLoadOf(Material.valueOf(material))
    }

    fun addLoad(material: Material, toAdd : Double){
        val current = currentLoads[material]!!
        currentLoads[material] = current + toAdd
    }

    fun addLoad(material: String, toAdd: Double) {
        return addLoad(Material.valueOf(material), toAdd)
    }

    fun setLoadOf(material: Material, value : Double) {
        currentLoads[material] = value
    }

    fun setLoadOf(material: String, value: Double){
        setLoadOf(Material.valueOf(material), value)
    }

    fun canStore(material: Material, max : Double, load : Double) : Boolean {
        return currentLoads[material]!! + load <= max
    }

    fun canStore(material: String, max : Double, load: Double) : Boolean {
        return canStore(Material.valueOf(material), max, load)
    }

    fun toStringRepresentation() : String {
        val jsonObj = JsonObject()
        currentLoads.forEach { jsonObj.addProperty(it.key.toString(), it.value.toString()) }
        return jsonObj.toString()
    }
}