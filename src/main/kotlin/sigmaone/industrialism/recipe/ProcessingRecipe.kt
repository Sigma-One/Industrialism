package sigmaone.industrialism.recipe

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.registry.Registry
import net.minecraft.world.World


abstract class ProcessingRecipe(
    val recipeType: RecipeType<*>,
    val name: Identifier,
    val inputs: List<Ingredient>,
    val outputStack: ItemStack,
    val processingTime: Int,
):
    Recipe<Inventory>
{
    override fun matches(inv: Inventory, world: World?): Boolean {
        if (inv.size() < inputs.size + 1) { return false }
        for ((i, input) in inputs.withIndex()) {
            if (!input.test(inv.getStack(i))) {
                return false
            }
        }
        return true
    }

    override fun craft(inv: Inventory): ItemStack? {
        return ItemStack.EMPTY
    }

    override fun fits(width: Int, height: Int): Boolean {
        return false
    }

    override fun getOutput(): ItemStack {
        return outputStack
    }

    override fun getId(): Identifier {
        return name
    }

    override fun getType(): RecipeType<*>? {
        return recipeType
    }

    override fun getPreviewInputs(): DefaultedList<Ingredient> {
        val dl = DefaultedList.of<Ingredient>()
        for (i in inputs) {
            dl.add(i)
        }
        return dl
    }
}

class ProcessingRecipeJsonFormat(
    var ingredients: List<JsonObject>,
    var output: String,
    var amount: Int,
    var processingTime: Int,
)

class ProcessingRecipeSerializer<T : ProcessingRecipe>(val factory: Factory<T>): RecipeSerializer<T> {
    // JSON -> Recipe
    override fun read(id: Identifier, json: JsonObject): T {
        val recipeJson: ProcessingRecipeJsonFormat = Gson().fromJson(json, ProcessingRecipeJsonFormat::class.java)
        var amount = recipeJson.amount
        val ingredients = ArrayList<Ingredient>()
        for (i in recipeJson.ingredients) {
            ingredients.add(Ingredient.fromJson(i))
        }
        if (amount == 0) { amount = 1 }
        val output = ItemStack(Registry.ITEM.getOrEmpty(Identifier(recipeJson.output)).get(), amount)
        val time = recipeJson.processingTime
        return factory.createRecipe(id, ingredients, output, time)
    }

    // Recipe -> PacketByteBuf
    override fun read(id: Identifier, buf: PacketByteBuf): T {
        val inputCount = buf.readVarInt()
        val ingredients = ArrayList<Ingredient>()
        for (i in 0 until inputCount) {
            ingredients.add(Ingredient.fromPacket(buf))
        }
        val output = buf.readItemStack()
        val time = buf.readVarInt()

        return factory.createRecipe(id, ingredients, output, time)
    }

    // PacketByteBuf -> Recipe
    override fun write(buf: PacketByteBuf, recipe: T) {
        buf.writeVarInt(recipe.inputs.size - 1)
        for (i in recipe.inputs) {
            i.write(buf)
        }
        buf.writeItemStack(recipe.outputStack)
        buf.writeVarInt(recipe.processingTime)
    }

    interface Factory<T : ProcessingRecipe> {
        fun createRecipe(
            id: Identifier,
            inputs: ArrayList<Ingredient>,
            output: ItemStack,
            processingTime: Int): T
    }
}