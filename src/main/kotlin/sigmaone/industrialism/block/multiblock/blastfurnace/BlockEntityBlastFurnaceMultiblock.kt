package sigmaone.industrialism.block.multiblock.blastfurnace

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.Item
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
import net.minecraft.world.World
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.Industrialism.BLAST_FURNACE_MULTIBLOCK
import sigmaone.industrialism.block.multiblock.BlockEntityMultiblockRoot
import sigmaone.industrialism.recipe.BlastingRecipe
import sigmaone.industrialism.util.IInventory

class BlockEntityBlastFurnaceMultiblock(blockPos: BlockPos?, blockState: BlockState?) :
    BlockEntityMultiblockRoot(
        BLAST_FURNACE_MULTIBLOCK,
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
    val items: DefaultedList<ItemStack> = DefaultedList.ofSize(3, ItemStack.EMPTY)
    var progress = 0
    var burnProgress = 100
    var startedProcessing: Long = 0
    var startedBurning: Long = 0
    var isProcessing = false
    var isBurning = false
    var burnTime = 0
    val acceptedFuels: HashSet<Item> = hashSetOf(
            Industrialism.COKE,
            Industrialism.COKE_BLOCK.asItem()
    )

    @JvmField
    val propertyDelegate: PropertyDelegate = object : PropertyDelegate {
        override fun get(index: Int): Int {
            return when (index) {
                1 -> progress
                0 -> 100
                2 -> burnProgress
                else -> throw IndexOutOfBoundsException("Invalid PropertyDelegate index")
            }
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                1 -> progress = value
                2 -> burnProgress = value
                else -> throw IndexOutOfBoundsException("Invalid PropertyDelegate index")
            }
        }

        override fun size(): Int {
            return 3
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
        return BlastFurnaceGuiDescription(syncId, inv, ScreenHandlerContext.create(world, pos))
    }

    override fun readNbt(tag: NbtCompound?) {
        super.readNbt(tag)
        Inventories.readNbt(tag, items)
        startedProcessing = tag!!.getLong("started_processing")
        startedBurning = tag!!.getLong("started_burning")

    }

    override fun writeNbt(tag: NbtCompound?): NbtCompound {
        Inventories.writeNbt(tag, items)
        tag!!.putLong("started_processing", startedProcessing)
        tag!!.putLong("started_burning", startedBurning)
        return super.writeNbt(tag)
    }

    override fun fromClientTag(tag: NbtCompound?) {
        isBurning = tag!!.getBoolean("is_burning")
    }

    override fun toClientTag(tag: NbtCompound?): NbtCompound {
        tag!!.putBoolean("is_burning", startedBurning != 0L)
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

    companion object {
        fun tick(world: World, pos: BlockPos, state: BlockState, entity: BlockEntityBlastFurnaceMultiblock) {
            val recipe: Recipe<*>? =
                world.recipeManager.getFirstMatch(Industrialism.BLASTING_RECIPE_TYPE, entity, world).orElse(null)
            val currentTime = world.time

            // Check:
            // * Is recipe a thing
            // * Can recipe be crafted
            // * No crafting in progress
            if (recipe != null && entity.canProcessRecipe(recipe) && entity.startedProcessing == 0L) {
                // Make sure furnace is fueled
                if (entity.startedBurning == 0L) {
                    if (!entity.items[2].isEmpty && entity.items[2].item in entity.acceptedFuels) {
                        entity.startedBurning = world.time
                        entity.burnTime = FuelRegistry.INSTANCE.get(entity.items[2].item as ItemConvertible)
                        entity.items[2].decrement(1)
                        world.setBlockState(pos, world.getBlockState(pos).with(Properties.LIT, true))
                    }
                }
                // Check for fuel level and start processing
                if (entity.startedBurning != 0L) {
                    entity.startedProcessing = world.time
                }
                entity.refresh()
            }
            // Check:
            // * Is recipe a thing
            // * Can recipe be crafted
            // * No crafting in progress
            else if (recipe != null && entity.canProcessRecipe(recipe) && entity.startedProcessing != 0L) {
                // If processing is done, do craft
                if (entity.startedProcessing + (recipe as BlastingRecipe).processingTime < currentTime) {
                    entity.startedProcessing = 0
                    entity.progress = 0
                    entity.items[0].decrement(1)
                    if (entity.items[1].isEmpty) {
                        entity.items[1] = ItemStack(recipe.output.item as ItemConvertible, recipe.output.count)
                    } else {
                        entity.items[1].increment(1)
                    }
                }
                // Otherwise, increment progress
                else {
                    entity.progress = (((currentTime - entity.startedProcessing).toFloat() / recipe.processingTime) * 100).toInt()
                }
            }
            // Increment burning progress
            if (entity.startedBurning != 0L) {
                entity.burnProgress = (((currentTime - entity.startedBurning).toFloat() / entity.burnTime) * 100).toInt()
            }
            // Handle running out of fuel and stop processing
            if (entity.startedBurning + entity.burnTime < currentTime && entity.startedBurning != 0L) {
                // Attempt to refuel
                if (!entity.items[2].isEmpty && entity.items[2].item in entity.acceptedFuels) {
                    entity.startedBurning = world.time
                    entity.burnTime = FuelRegistry.INSTANCE.get(entity.items[2].item as ItemConvertible)
                    entity.items[2].decrement(1)
                }
                // Otherwise end processing
                else {
                    entity.startedBurning = 0L
                    entity.startedProcessing = 0L
                    entity.burnProgress = 100
                    entity.progress = 0
                    world.setBlockState(pos, world.getBlockState(pos).with(Properties.LIT, false))
                }
                entity.refresh()
            }
            if (recipe == null || !entity.canProcessRecipe(recipe)) {
                entity.progress = 0
                entity.startedProcessing = 0
                entity.refresh()
            }
        }
    }
}