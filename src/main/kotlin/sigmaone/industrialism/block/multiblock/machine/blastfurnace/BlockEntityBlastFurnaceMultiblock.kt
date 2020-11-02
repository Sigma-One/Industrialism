package sigmaone.industrialism.block.multiblock.machine.blastfurnace

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
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
import net.minecraft.state.property.Properties
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Tickable
import net.minecraft.util.collection.DefaultedList
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.Industrialism.Companion.BLAST_FURNACE_MULTIBLOCK
import sigmaone.industrialism.Industrialism.Companion.BLAST_FURNACE_MULTIBLOCK_BLOCK
import sigmaone.industrialism.Industrialism.Companion.COKE_OVEN_MULTIBLOCK
import sigmaone.industrialism.block.multiblock.BlockEntityMultiblockRootBase
import sigmaone.industrialism.block.multiblock.machine.cokeoven.CokeOvenGuiDescription
import sigmaone.industrialism.recipe.CokingRecipe
import sigmaone.industrialism.util.IInventory

class BlockEntityBlastFurnaceMultiblock : BlockEntityMultiblockRootBase(BLAST_FURNACE_MULTIBLOCK), IInventory, NamedScreenHandlerFactory, Tickable, PropertyDelegateHolder, BlockEntityClientSerializable {
    @JvmField
    val items: DefaultedList<ItemStack> = DefaultedList.ofSize(3, ItemStack.EMPTY)
    var progress = 0
    var burnProgress = 0
    var startedProcessing: Long = 0
    var isProcessing = false

    @JvmField
    val propertyDelegate: PropertyDelegate = object : PropertyDelegate {
        override fun get(index: Int): Int {
            return when (index) {
                0 -> progress
                1 -> burnProgress
                2 -> 100
                else -> throw IndexOutOfBoundsException("Invalid PropertyDelegate index")
            }
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                0 -> progress = value
                1 -> burnProgress = value
                else -> throw IndexOutOfBoundsException("Invalid PropertyDelegate index")
            }
        }

        override fun size(): Int {
            return 2
        }
    }

    fun refresh() {
        markDirty()
        if (getWorld() != null && !getWorld()!!.isClient) {
            sync()
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
        startedProcessing = tag!!.getLong("started_processing")
    }

    override fun toTag(tag: CompoundTag?): CompoundTag {
        Inventories.toTag(tag, items)
        tag!!.putLong("started_processing", startedProcessing)
        return super.toTag(tag)
    }

    override fun fromClientTag(tag: CompoundTag?) {
        isProcessing = tag!!.getBoolean("is_processing")
    }

    override fun toClientTag(tag: CompoundTag?): CompoundTag {
        tag!!.putBoolean("is_processing", startedProcessing != 0L)
        return tag
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
        var recipe: Recipe<*>? = world!!.recipeManager.getFirstMatch(Industrialism.BLASTING_RECIPE_TYPE, this, world).orElse(null)
        val currentTime = world!!.time
        if (recipe != null && canProcessRecipe(recipe) && startedProcessing == 0L) {
            startedProcessing = world!!.time
            refresh()
        }
        else if (recipe != null && startedProcessing != 0L) {
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
                recipe = null
            }
            else {
                progress = (((currentTime - startedProcessing).toFloat() / recipe.cookTime) * 100).toInt()
            }
        }
        if (recipe == null) {
            progress = 0
            startedProcessing = 0
            refresh()
        }
    }
}