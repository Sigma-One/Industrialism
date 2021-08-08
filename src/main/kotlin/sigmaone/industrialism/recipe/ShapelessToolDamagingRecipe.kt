package sigmaone.industrialism.recipe

import com.google.gson.JsonObject
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.ShearsItem
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.ShapelessRecipe
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import sigmaone.industrialism.Industrialism
import java.util.*

class ShapelessToolDamagingRecipe(
    id: Identifier?,
    group: String?,
    output: ItemStack?,
    input: DefaultedList<Ingredient>?
): ShapelessRecipe(id, group, output, input) {
    override fun getRemainder(inventory: CraftingInventory?): DefaultedList<ItemStack> {
        val defaultedList = DefaultedList.ofSize(inventory!!.size(), ItemStack.EMPTY)

        for (i in defaultedList.indices) {
            val item = inventory.getStack(i)
            if (item.item == Industrialism.FORGE_HAMMER || item.item is ShearsItem) {
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
        return Industrialism.SHAPELESS_TOOL_DAMAGING_RECIPE_SERIALIZER
    }
}

class ShapelessToolDamagingRecipeSerializer(): ShapelessRecipe.Serializer() {
    override fun read(identifier: Identifier?, jsonObject: JsonObject?): ShapelessToolDamagingRecipe {
        val recipe = super.read(identifier, jsonObject)
        return ShapelessToolDamagingRecipe(recipe.id, recipe.group, recipe.output, recipe.ingredients)
    }

    override fun read(identifier: Identifier?, packetByteBuf: PacketByteBuf?): ShapelessToolDamagingRecipe {
        val recipe = super.read(identifier, packetByteBuf)
        return ShapelessToolDamagingRecipe(recipe.id, recipe.group, recipe.output, recipe.ingredients)
    }
}