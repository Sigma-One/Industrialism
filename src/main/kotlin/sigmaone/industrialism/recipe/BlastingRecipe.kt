package sigmaone.industrialism.recipe

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import sigmaone.industrialism.Industrialism

class BlastingRecipe(
    id: Identifier,
    inputs: ArrayList<Ingredient>,
    output: ItemStack,
    processingTime: Int
) :
    ProcessingRecipe(
        Industrialism.BLASTING_RECIPE_TYPE,
        id,
        inputs,
        output,
        processingTime
    )
{
    @Environment(EnvType.CLIENT)
    override fun getRecipeKindIcon(): ItemStack {
        return Industrialism.BLAST_FURNACE_DUMMY_ITEM.defaultStack
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return Industrialism.BLASTING_RECIPE_SERIALIZER
    }

    override fun getType(): RecipeType<*> {
        return Industrialism.BLASTING_RECIPE_TYPE
    }
}