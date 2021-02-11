package sigmaone.industrialism.util.datagen.model

import com.google.gson.JsonObject
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.registry.Registry
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

    fun generateBlockItemModel(id: Identifier, isTool: Boolean = false) {
        val jsonRoot = JsonObject()
        jsonRoot.addProperty("parent", Identifier(id.namespace, "block/${id.path}").toString())

        DataGenerator.ITEM_MODELS[id] = jsonRoot
    }

    fun generateItemModel(item: Item, texture: Identifier, isTool: Boolean = false) {
        val jsonRoot = JsonObject()
        jsonRoot.addProperty("parent", if (isTool) "item/handheld" else "item/generated")
        val jsonTextures = JsonObject()
        jsonTextures.addProperty("layer0", Identifier(texture.namespace, "item/${texture.path}").toString())
        jsonRoot.add("textures", jsonTextures)

        DataGenerator.ITEM_MODELS[Registry.ITEM.getId(item)] = jsonRoot
    }

    fun generateItemModel(item: Item, isTool: Boolean = false) {
        val id = Registry.ITEM.getId(item)
        generateItemModel(item, id, isTool)
    }

    fun generateToolItemModel(item: Item) {
        val id = Registry.ITEM.getId(item)
        val jsonRoot = JsonObject()
        jsonRoot.addProperty("parent", "item/handheld")
        val jsonTextures = JsonObject()
        jsonTextures.addProperty("layer0", Identifier(id.namespace, "item/${id.path}").toString())
        jsonRoot.add("textures", jsonTextures)

        DataGenerator.ITEM_MODELS[id] = jsonRoot
    }
}