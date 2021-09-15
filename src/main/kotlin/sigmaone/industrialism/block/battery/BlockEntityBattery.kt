package sigmaone.industrialism.block.battery

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemUsageContext
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.block.IBlockEntityConfigurable
import sigmaone.industrialism.block.IBlockEntityRefreshable
import sigmaone.industrialism.component.energy.ComponentEnergyContainer
import sigmaone.industrialism.component.energy.IComponentEnergyContainer
import sigmaone.industrialism.util.IO
import team.reborn.energy.EnergyTier

class BlockEntityBattery(blockPos: BlockPos?, blockState: BlockState?) :
    BlockEntity(Industrialism.BATTERY, blockPos, blockState),
    IComponentEnergyContainer<BlockEntityBattery>,
    IBlockEntityConfigurable,
    BlockEntityClientSerializable,
    IBlockEntityRefreshable
{
    override val componentEnergyContainer = ComponentEnergyContainer(
        this,
        EnergyTier.LOW,
        32000.0,
        0.0,
        hashMapOf(
            Direction.NORTH to IO.NONE,
            Direction.SOUTH to IO.NONE,
            Direction.EAST to IO.NONE,
            Direction.WEST to IO.NONE,
            Direction.UP   to IO.NONE,
            Direction.DOWN to IO.NONE
        )
    )

    companion object {
        fun tick(entity: BlockEntityBattery) {
            entity.componentEnergyContainer.tick()
        }
    }

    override fun writeNbt(tag: NbtCompound): NbtCompound {
        super.writeNbt(tag)
        return componentEnergyContainer.writeNbt(tag)
    }

    override fun readNbt(tag: NbtCompound) {
        super.readNbt(tag)
        componentEnergyContainer.readNbt(tag)
    }

    override fun fromClientTag(tag: NbtCompound) {
        componentEnergyContainer.fromClientTag(tag)
    }

    override fun toClientTag(tag: NbtCompound): NbtCompound {
        return componentEnergyContainer.toClientTag(tag)
    }

    override fun configure(side: Direction, context: ItemUsageContext?) {
        componentEnergyContainer.sideConfig[side] = componentEnergyContainer.sideConfig[side]!!.next()
    }


    override fun refresh() {
        markDirty()
        if (world != null && !world!!.isClient) {
            sync()
        }
    }
}