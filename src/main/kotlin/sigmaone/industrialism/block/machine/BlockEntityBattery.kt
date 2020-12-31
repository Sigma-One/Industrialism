package sigmaone.industrialism.block.machine

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemUsageContext
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Tickable
import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.block.IBlockEntityConfigurable
import sigmaone.industrialism.block.IBlockEntityRefreshable
import sigmaone.industrialism.component.energy.ComponentEnergyContainer
import sigmaone.industrialism.component.energy.IComponentEnergyContainer
import sigmaone.industrialism.util.IO
import team.reborn.energy.EnergyTier

class BlockEntityBattery :
    BlockEntity(Industrialism.BATTERY),
    IComponentEnergyContainer,
    IBlockEntityConfigurable,
    Tickable,
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

    override fun tick() {
        componentEnergyContainer.tick()
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)
        return componentEnergyContainer.toTag(tag)
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)
        componentEnergyContainer.fromTag(state, tag)
    }

    override fun fromClientTag(tag: CompoundTag) {
        componentEnergyContainer.fromClientTag(tag)
    }

    override fun toClientTag(tag: CompoundTag): CompoundTag {
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