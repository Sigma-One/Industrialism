package sigmaone.industrialism.util.datagen

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.swordglowsblue.artifice.api.Artifice
import com.swordglowsblue.artifice.api.resource.StringResource
import net.minecraft.util.Identifier
import sigmaone.industrialism.Industrialism

object DataGenerator {
    val RECIPES: HashMap<Identifier, JsonObject> = hashMapOf()
    val ITEM_TAGS: HashMap<Identifier, JsonArray> = hashMapOf()
    val BLOCK_TAGS: HashMap<Identifier, JsonArray> = hashMapOf()

    val BLOCKSTATES: HashMap<Identifier, JsonObject> = hashMapOf()
    val BLOCK_MODELS: HashMap<Identifier, JsonObject> = hashMapOf()
    val ITEM_MODELS: HashMap<Identifier, JsonObject> = hashMapOf()

    val gson = GsonBuilder().setPrettyPrinting().create()

    fun commit() {
        val datapack = Artifice.registerDataPack(Identifier(Industrialism.MOD_ID, "data")) { pack ->
            pack.setDisplayName("Industrialism Data")
            pack.setDescription("Default data for Industrialism")

            for ((id, recipe) in RECIPES) {
                pack.add(Identifier(id.namespace, "recipes/${id.path}.json"), StringResource(gson.toJson(recipe)))
            }

            for ((id, tag) in ITEM_TAGS) {
                val json = JsonObject()
                json.addProperty("replace", false)
                json.add("values", tag)
                pack.add(Identifier(id.namespace, "tags/items/${id.path}.json"), StringResource(gson.toJson(json)))
            }

            for ((id, tag) in BLOCK_TAGS) {
                val json = JsonObject()
                json.addProperty("replace", false)
                json.add("values", tag)
                pack.add(Identifier(id.namespace, "tags/blocks/${id.path}.json"), StringResource(gson.toJson(json)))
            }
        }
    }

    fun commitClient() {
        val resourcepack = Artifice.registerAssetPack(Identifier(Industrialism.MOD_ID, "assets")) { pack ->
            pack.setDisplayName("Industrialism Assets")
            pack.setDescription("Default assets for Industrialism")

            for ((id, model) in BLOCK_MODELS) {
                pack.add(Identifier(id.namespace, "models/block/${id.path}.json"), StringResource(gson.toJson(model)))
            }

            for ((id, model) in ITEM_MODELS) {
                pack.add(Identifier(id.namespace, "models/item/${id.path}.json"), StringResource(gson.toJson(model)))
            }

            for ((id, state) in BLOCKSTATES) {
                pack.add(Identifier(id.namespace, "blockstates/${id.path}.json"), StringResource(gson.toJson(state)))
            }
        }
    }
}