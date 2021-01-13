package sigmaone.industrialism.util.datagen.model

import com.google.gson.JsonObject
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import sigmaone.industrialism.util.datagen.DataGenerator

object ModelGenerator {
    fun generateBlockModel(id: Identifier, texture: Identifier = id) {
        val jsonRoot = JsonObject()
        jsonRoot.addProperty("parent", "block/cube_all")
        val jsonTextures = JsonObject()
        jsonTextures.addProperty("all", Identifier(texture.namespace, "block/${texture.path}").toString())
        jsonRoot.add("textures", jsonTextures)

        DataGenerator.BLOCK_MODELS[id] = jsonRoot
    }

    @JvmName("generateBlockModelSided")
    fun generateBlockModel(id: Identifier, textures: HashMap<Direction, Identifier>) {
        val jsonRoot = JsonObject()
        jsonRoot.addProperty("parent", "block/cube_all")
        val jsonTextures = JsonObject()
        for ((dir, tex) in textures) {
            jsonTextures.addProperty(dir.toString().toLowerCase(), Identifier(tex.namespace, "block/${tex.path}").toString())
        }
        jsonRoot.add("textures", jsonTextures)

        DataGenerator.BLOCK_MODELS[id] = jsonRoot
    }

    fun generateBlockItemModel(id: Identifier) {
        val jsonRoot = JsonObject()
        jsonRoot.addProperty("parent", Identifier(id.namespace, "block/${id.path}").toString())

        DataGenerator.ITEM_MODELS[id] = jsonRoot
    }

    fun generateItemModel(id: Identifier, texture: Identifier) {
        val jsonRoot = JsonObject()
        jsonRoot.addProperty("parent", "item/generated")
        val jsonTextures = JsonObject()
        jsonTextures.addProperty("layer0", Identifier(texture.namespace, "item/${texture.path}").toString())
        jsonRoot.add("textures", jsonTextures)

        DataGenerator.ITEM_MODELS[id] = jsonRoot
    }

    fun generateToolItemModel(id: Identifier, texture: Identifier) {
        val jsonRoot = JsonObject()
        jsonRoot.addProperty("parent", "item/handheld")
        val jsonTextures = JsonObject()
        jsonTextures.addProperty("layer0", Identifier(texture.namespace, "item/${texture.path}").toString())
        jsonRoot.add("textures", jsonTextures)

        DataGenerator.ITEM_MODELS[id] = jsonRoot
    }
}