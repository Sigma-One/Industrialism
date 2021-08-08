package sigmaone.industrialism.block.multiblock.cokeoven

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.recipe.Recipe
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.state.property.Properties
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.Industrialism.COKE_OVEN_MULTIBLOCK
import sigmaone.industrialism.block.multiblock.BlockEntityMultiblockRoot
import sigmaone.industrialism.recipe.CokingRecipe
import sigmaone.industrialism.util.IInventory

class BlockEntityCokeOvenMultiblock(blockPos: BlockPos?, blockState: BlockState?) :
    BlockEntityMultiblockRoot(
        COKE_OVEN_MULTIBLOCK,
        blockPos,
        blockState
    ),
    IInventory,
    SidedInventory,
    NamedScreenHandlerFactory,
    PropertyDelegateHolder,
    BlockEntityClientSerializable
{
    @JvmField
    val items: DefaultedList<ItemStack> = DefaultedList.ofSize(2, ItemStack.EMPTY)
    var progress = 0
    var startedProcessing: Long = 0
    var isProcessing = false

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
        super<BlockEntityMultiblockRoot>.markDirty()
    }

    override fun getAvailableSlots(side: Direction?): IntArray {
        return when (side) {
            world!!.getBlockState(pos).get(Properties.HORIZONTAL_FACING).opposite -> intArrayOf(1)
            else -> intArrayOf()
        }
    }

    override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
        return false
    }

    override fun canExtract(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
        return when (dir) {
            world!!.getBlockState(pos).get(Properties.HORIZONTAL_FACING).opposite -> true
            else -> false
        }
    }

    override fun getDisplayName(): Text {
        return TranslatableText(cachedState.block.translationKey)
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory?, player: PlayerEntity?): ScreenHandler? {
        return CokeOvenGuiDescription(syncId, inv, ScreenHandlerContext.create(world, pos))
    }

    override fun readNbt(tag: NbtCompound?) {
        super.readNbt(tag)
        Inventories.readNbt(tag, items)
        startedProcessing = tag!!.getLong("started_processing")
    }

    override fun writeNbt(tag: NbtCompound?): NbtCompound {
        Inventories.writeNbt(tag, items)
        tag!!.putLong("started_processing", startedProcessing)
        return super.writeNbt(tag)
    }

    override fun fromClientTag(tag: NbtCompound?) {
        isProcessing = tag!!.getBoolean("is_processing")
    }

    override fun toClientTag(tag: NbtCompound?): NbtCompound {
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

    fun tick() {
        val recipe: Recipe<*>? = world!!.recipeManager.getFirstMatch(Industrialism.COKING_RECIPE_TYPE, this, world).orElse(null)
        val currentTime = world!!.time
        // Check:
        // * Is recipe a thing
        // * Can recipe be crafted
        // * No crafting in progress
        if (!world!!.isClient && startedProcessing == 0L) {
            world!!.setBlockState(pos, world!!.getBlockState(pos).with(Properties.LIT, false))
        }
        if (recipe != null && canProcessRecipe(recipe) && startedProcessing == 0L) {
            world!!.setBlockState(pos, world!!.getBlockState(pos).with(Properties.LIT, true))
            startedProcessing = world!!.time
            refresh()
        }
        // Check:
        // * Is recipe a thing
        // * Can recipe be crafted
        // * No crafting in progress
        else if (recipe != null && canProcessRecipe(recipe) && startedProcessing != 0L) {
            // If processing is done, do craft
            if (startedProcessing + (recipe as CokingRecipe).processingTime < currentTime) {
                startedProcessing = 0
                progress = 0
                items[0].decrement(1)
                if (items[1].isEmpty) {
                    items[1] = ItemStack(recipe.output.item as ItemConvertible, recipe.output.count)
                }
                else {
                    items[1].increment(1)
                }
                world!!.setBlockState(pos, world!!.getBlockState(pos).with(Properties.LIT, false))
            }
            // Otherwise, increment progress
            else {
                progress = (((currentTime - startedProcessing).toFloat() / recipe.processingTime) * 100).toInt()
            }
        }
        if (recipe == null || !canProcessRecipe(recipe)) {
            progress = 0
            startedProcessing = 0
            refresh()
        }
    }
}