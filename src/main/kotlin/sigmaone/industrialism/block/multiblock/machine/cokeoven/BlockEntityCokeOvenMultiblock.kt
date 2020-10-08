package sigmaone.industrialism.block.multiblock.machine.cokeoven

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.recipe.Recipe
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Tickable
import net.minecraft.util.collection.DefaultedList
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.Industrialism.Companion.COKE_OVEN_MULTIBLOCK
import sigmaone.industrialism.block.multiblock.BlockEntityMultiblockRootBase
import sigmaone.industrialism.recipe.CokingRecipe
import sigmaone.industrialism.util.IInventory

class BlockEntityCokeOvenMultiblock : BlockEntityMultiblockRootBase(COKE_OVEN_MULTIBLOCK), IInventory, NamedScreenHandlerFactory, Tickable, PropertyDelegateHolder {
    @JvmField
    val items: DefaultedList<ItemStack> = DefaultedList.ofSize(2, ItemStack.EMPTY)
    var progress = 0
    var startedProcessing: Long = 0

    @JvmField
    val propertyDelegate: PropertyDelegate = object : PropertyDelegate {
        override fun get(index: Int): Int {
            return when (index) {
                0 -> progress
                1 -> 100
                else -> throw IndexOutOfBoundsException("Invalid PropertyDelegate index")
            }
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                0 -> progress = value
                else -> throw IndexOutOfBoundsException("Invalid PropertyDelegate index")
            }
        }

        override fun size(): Int {
            return 2
        }
    }

    override fun getPropertyDelegate(): PropertyDelegate {
        return propertyDelegate
    }

    override fun getItems(): DefaultedList<ItemStack> {
        return items
    }

    override fun markDirty() {
        super<BlockEntityMultiblockRootBase>.markDirty()
    }

    override fun getDisplayName(): Text {
        return TranslatableText(cachedState.block.translationKey)
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory?, player: PlayerEntity?): ScreenHandler? {
        return CokeOvenGuiDescription(syncId, inv, ScreenHandlerContext.create(world, pos))
    }

    override fun fromTag(state: BlockState?, tag: CompoundTag?) {
        super.fromTag(state, tag)
        Inventories.fromTag(tag, items)
        startedProcessing = tag!!.getLong("startedProcessing")
    }

    override fun toTag(tag: CompoundTag?): CompoundTag {
        Inventories.toTag(tag, items)
        tag!!.putLong("startedProcessing", startedProcessing)
        return super.toTag(tag)
    }

    private fun canProcessRecipe(recipe: Recipe<*>): Boolean {
        if (items[0].isEmpty) {
            return false
        }
        if (items[1].count >= items[1].maxCount) {
            return false
        }
        if (!items[1].isEmpty && items[1].item != recipe.output.item) {
            return false
        }
        return true
    }

    override fun tick() {
        val recipe: Recipe<*>? = world!!.recipeManager.getFirstMatch(Industrialism.COKING_RECIPE_TYPE, this, world).orElse(null)
        val currentTime = world!!.time
        if (recipe != null && canProcessRecipe(recipe) && startedProcessing == 0L) {
            startedProcessing = world!!.time
            markDirty()
        }
        else if (startedProcessing != 0L) {
            if (startedProcessing + (recipe as CokingRecipe).cookTime < currentTime) {
                startedProcessing = 0
                progress = 0
                if (canProcessRecipe(recipe)) {
                    items[0].decrement(1)
                    if (items[1].item == recipe.output.item) {
                        items[1].increment(1)
                    }
                    else {
                        items[1] = ItemStack(recipe.output.item)
                    }
                }
            }

            progress = if (items[0].isEmpty) {
                0
            }
            else {
                (((currentTime - startedProcessing).toFloat() / recipe.cookTime) * 100).toInt()
            }
        }
    }
}