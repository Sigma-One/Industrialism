package sigmaone.industrialism.recipe

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import sigmaone.industrialism.Industrialism

class CokingRecipe(
    id: Identifier,
    inputs: ArrayList<Ingredient>,
    output: ItemStack,
    processingTime: Int
) :
    ProcessingRecipe(
        Industrialism.COKING_RECIPE_TYPE,
        id,
        inputs,
        output,
        processingTime
    )
{
    @Environment(EnvType.CLIENT)
    override fun createIcon(): ItemStack {
        return Industrialism.COKE_OVEN_DUMMY_ITEM.defaultStack
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return Industrialism.COKING_RECIPE_SERIALIZER
    }

    override fun getType(): RecipeType<*> {
        return Industrialism.COKING_RECIPE_TYPE
    }
}