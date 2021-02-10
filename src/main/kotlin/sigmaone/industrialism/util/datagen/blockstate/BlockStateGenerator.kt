package sigmaone.industrialism.util.datagen.blockstate

import com.google.gson.JsonObject
import net.minecraft.util.Identifier
import sigmaone.industrialism.util.datagen.DataGenerator

object BlockStateGenerator {

    fun generateBlockState(blockId: Identifier, variants: HashMap<String, Identifier>) {
        val jsonRoot = JsonObject()
        val jsonVariants = JsonObject()

        for ((name, variant) in variants) {
            val jsonVariant = JsonObject()
            jsonVariant.addProperty("model", "${variant.namespace}:block/${variant.path}")
            jsonVariants.add(name, jsonVariant)
        }
        jsonRoot.add("variants", jsonVariants)

        DataGenerator.BLOCKSTATES[blockId] = jsonRoot
    }

    @JvmName("generateBlockStateFull")
    fun generateBlockState(blockId: Identifier, variants: HashMap<String, Triple<Identifier, Int, Int>>) {
        val jsonRoot = JsonObject()
        val jsonVariants = JsonObject()

        for ((name, variant) in variants) {
            val jsonVariant = JsonObject()
            jsonVariant.addProperty("model", "${variant.first.namespace}:block/${variant.first.path}")
            jsonVariant.addProperty("x", variant.second)
            jsonVariant.addProperty("y", variant.third)
            jsonVariants.add(name, jsonVariant)
        }
        jsonRoot.add("variants", jsonVariants)

        DataGenerator.BLOCKSTATES[blockId] = jsonRoot
    }
}