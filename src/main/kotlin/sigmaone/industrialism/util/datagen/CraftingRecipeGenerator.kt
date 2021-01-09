package sigmaone.industrialism.util.datagen

import com.swordglowsblue.artifice.api.builder.data.recipe.ShapedRecipeBuilder
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
}