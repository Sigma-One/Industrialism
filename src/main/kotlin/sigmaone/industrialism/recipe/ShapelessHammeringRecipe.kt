package sigmaone.industrialism.recipe

import com.google.gson.JsonObject
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.ShapelessRecipe
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import sigmaone.industrialism.Industrialism
import java.util.*

class ShapelessHammeringRecipe(id: Identifier?, group: String?, output: ItemStack?, input: DefaultedList<Ingredient>?): ShapelessRecipe(id, group, output, input) {
    override fun getRemainingStacks(inventory: CraftingInventory?): DefaultedList<ItemStack> {
        val defaultedList = DefaultedList.ofSize(inventory!!.size(), ItemStack.EMPTY)

        for (i in defaultedList.indices) {
            val item = inventory.getStack(i)
            if (item.item == Industrialism.FORGE_HAMMER) {
                defaultedList[i] = item.copy()
                defaultedList[i].damage(1, Random(), null)
                if (defaultedList[i].damage >= item.maxDamage) {
                    defaultedList[i] = ItemStack.EMPTY
                }
            }
            else if (item.item.hasRecipeRemainder()) {
                defaultedList[i] = ItemStack(item.item.recipeRemainder)
            }
        }

        return defaultedList
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return Industrialism.SHAPELESS_HAMMERING_RECIPE_SERIALIZER
    }
}

class ShapelessHammeringRecipeSerializer(): ShapelessRecipe.Serializer() {
    override fun read(identifier: Identifier?, jsonObject: JsonObject?): ShapelessHammeringRecipe {
        val recipe = super.read(identifier, jsonObject)
        return ShapelessHammeringRecipe(recipe.id, recipe.group, recipe.output, recipe.previewInputs)
    }

    override fun read(identifier: Identifier?, packetByteBuf: PacketByteBuf?): ShapelessHammeringRecipe {
        val recipe = super.read(identifier, packetByteBuf)
        return ShapelessHammeringRecipe(recipe.id, recipe.group, recipe.output, recipe.previewInputs)
    }
}