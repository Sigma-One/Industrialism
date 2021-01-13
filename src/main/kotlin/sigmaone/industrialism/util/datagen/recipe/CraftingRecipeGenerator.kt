package sigmaone.industrialism.util.datagen.recipe

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import sigmaone.industrialism.util.datagen.DataGenerator
import java.security.InvalidParameterException


object CraftingRecipeGenerator {
    fun generateShapedRecipe(
        name: Identifier,
        tagKeys: HashMap<Char, Identifier> = hashMapOf(),
        itemKeys: HashMap<Char, Item> = hashMapOf(),
        pattern: Array<String>,
        result: Item,
        resultCount: Int = 1,
        group: Identifier? = null
    ) {
        if (pattern.size != 3) { throw InvalidParameterException("Invalid number of recipe rows.\nRecipe should have 3 rows.") }

        val jsonRoot = JsonObject()
        jsonRoot.addProperty("type", "minecraft:crafting_shaped")

        if (group != null) {
            jsonRoot.addProperty("group", group.toString())
        }

        val jsonPattern = JsonArray()
        jsonPattern.add(pattern[0])
        jsonPattern.add(pattern[1])
        jsonPattern.add(pattern[2])

        jsonRoot.add("pattern", jsonPattern)

        val jsonKeys = JsonObject()

        for (key in tagKeys) {
            val jsonKey = JsonObject()
            jsonKey.addProperty("tag", key.value.toString())
            jsonKeys.add(key.key.toString(), jsonKey)
        }

        for (key in itemKeys) {
            val jsonKey = JsonObject()
            jsonKey.addProperty("item", Registry.ITEM.getId(key.value).toString())
            jsonKeys.add(key.key.toString(), jsonKey)
        }

        jsonRoot.add("key", jsonKeys)

        val jsonResult = JsonObject()
        jsonResult.addProperty("item", Registry.ITEM.getId(result).toString())
        jsonResult.addProperty("count", resultCount)

        jsonRoot.add("result", jsonResult)

        DataGenerator.RECIPES[name] = jsonRoot
    }

    fun generateShapelessRecipe(
        name: Identifier,
        tagIngredients: Array<Identifier> = arrayOf(),
        itemIngredients: Array<Item> = arrayOf(),
        result: Item,
        resultCount: Int = 1,
        group: Identifier? = null,
        damagesTools: Boolean = false
    ) {
        val jsonRoot = JsonObject()
        if (damagesTools) {
            jsonRoot.addProperty("type", "minecraft:crafting_shapeless")
        }
        else {
            jsonRoot.addProperty("type", "industrialism:crafting_shapeless_tooldamage")
        }

        if (group != null) {
            jsonRoot.addProperty("group", group.toString())
        }

        val jsonIngredients = JsonArray()

        for (tag in tagIngredients) {
            val jsonIngredient = JsonObject()
            jsonIngredient.addProperty("tag", tag.toString())
            jsonIngredients.add(jsonIngredient)
        }

        for (item in itemIngredients) {
            val jsonIngredient = JsonObject()
            jsonIngredient.addProperty("item", Registry.ITEM.getId(item).toString())
            jsonIngredients.add(jsonIngredient)
        }

        jsonRoot.add("ingredients", jsonIngredients)

        val jsonResult = JsonObject()
        jsonResult.addProperty("item", Registry.ITEM.getId(result).toString())
        jsonResult.addProperty("count", resultCount)

        jsonRoot.add("result", jsonResult)

        DataGenerator.RECIPES[name] = jsonRoot
    }
}