package sigmaone.industrialism.recipe

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.item.ItemStack
import net.minecraft.recipe.AbstractCookingRecipe
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.util.Identifier
import sigmaone.industrialism.Industrialism

class BlastingRecipe(
        id: Identifier?,
        group: String?,
        input: Ingredient?,
        output: ItemStack?,
        experience: Float,
        cookTime: Int
) : AbstractCookingRecipe(
        Industrialism.BLASTING_RECIPE_TYPE,
        id,
        group,
        input,
        output,
        experience,
        cookTime
) {
    @Environment(EnvType.CLIENT)
    override fun getRecipeKindIcon(): ItemStack {
        return ItemStack(Industrialism.STEEL.ingot)
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return Industrialism.BLASTING_RECIPE_SERIALIZER
    }
}