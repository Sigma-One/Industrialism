package sigmaone.industrialism.block.machine

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Tickable
import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.Industrialism.InputConfig
import sigmaone.industrialism.block.BlockEntityConnectableEnergyContainer
import sigmaone.industrialism.component.energy.ComponentEnergyContainer
import sigmaone.industrialism.component.energy.IComponentEnergyContainer
import sigmaone.industrialism.util.IO
import team.reborn.energy.EnergyTier

class BlockEntityManualGenerator : BlockEntity(Industrialism.MANUAL_GENERATOR), IComponentEnergyContainer, Tickable {
    override val componentEnergyContainer = ComponentEnergyContainer(
        this,
        EnergyTier.LOW,
        32.0,
        0.0,
        hashMapOf(
            Direction.NORTH to IO.OUTPUT,
            Direction.SOUTH to IO.OUTPUT,
            Direction.EAST  to IO.OUTPUT,
            Direction.WEST  to IO.OUTPUT,
            Direction.UP    to IO.NONE,
            Direction.DOWN  to IO.OUTPUT
        )
    )

    override fun tick() {
        componentEnergyContainer.tick()
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        return componentEnergyContainer.toTag(tag)
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        componentEnergyContainer.fromTag(state, tag)
    }
}