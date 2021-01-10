package sigmaone.industrialism.util.datagen

import com.swordglowsblue.artifice.api.builder.data.recipe.ShapedRecipeBuilder
import com.swordglowsblue.artifice.api.builder.data.recipe.ShapelessRecipeBuilder
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import sigmaone.industrialism.Industrialism
import java.security.InvalidParameterException


object CraftingRecipeGenerator {
    fun generateShapedRecipe(
        name: String,
        result: Item,
        resultCount: Int = 1,
        itemKey: HashMap<Char, Item> = hashMapOf(),
        tagKey: HashMap<Char, Identifier> = hashMapOf(),
        pattern: Array<String>,
        group: Identifier? = null
    ) {
        if (pattern.size != 3) { throw InvalidParameterException("Invalid number of recipe rows.\nRecipe should have 3 rows.") }

        val builder = ShapedRecipeBuilder()
        builder.pattern(pattern[0], pattern[1], pattern[2])

        for ((key, item) in itemKey) {
            builder.ingredientItem(key, Registry.ITEM.getId(item))
        }
        for ((key, id) in tagKey) {
            builder.ingredientTag(key, id)
        }

        builder.result(Registry.ITEM.getId(result), resultCount)

        if (group != null) {
            builder.group(group)
        }

        Industrialism.RESOURCES.add(
            Pair(
                Identifier(Industrialism.MOD_ID, "recipes/${name}.json"),
                builder.build()
            )
        )
    }

    fun generateShapelessRecipe(
        name: String,
        result: Item,
        resultCount: Int = 1,
        itemIngredients: Array<Item> = arrayOf(),
        tagIngredients: Array<Identifier> = arrayOf(),
        group: Identifier? = null,
        damagesTools: Boolean = false
    ) {
        val builder = ShapelessRecipeBuilder()
        if (damagesTools) {
            builder.type(Identifier(Industrialism.MOD_ID, "crafting_shapeless_tooldamage"))
        }

        for (item in itemIngredients) {
            builder.ingredientItem(Registry.ITEM.getId(item))
        }
        for (id in tagIngredients) {
            builder.ingredientTag(id)
        }

        builder.result(Registry.ITEM.getId(result), resultCount)

        if (group != null) {
            builder.group(group)
        }

        Industrialism.RESOURCES.add(
            Pair(
                Identifier(Industrialism.MOD_ID, "recipes/${name}.json"),
                builder.build()
            )
        )
    }
}