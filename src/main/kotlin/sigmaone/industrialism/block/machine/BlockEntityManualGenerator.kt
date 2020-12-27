package sigmaone.industrialism.block.machine

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Tickable
import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.block.IBlockEntityRefreshable
import sigmaone.industrialism.component.energy.ComponentEnergyContainer
import sigmaone.industrialism.component.energy.IComponentEnergyContainer
import sigmaone.industrialism.component.mechanical.ComponentMechanicalDevice
import sigmaone.industrialism.component.mechanical.IComponentMechanicalDevice
import sigmaone.industrialism.util.IO
import team.reborn.energy.EnergyTier

class BlockEntityManualGenerator :
    BlockEntity(Industrialism.MANUAL_GENERATOR),
    IComponentEnergyContainer,
    IComponentMechanicalDevice,
    Tickable,
    IBlockEntityRefreshable
{
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

    override val componentMechanicalDevice = ComponentMechanicalDevice(
        this,
        1.0,
        999.0,
        hashMapOf(
            Direction.UP    to IO.INPUT,
            Direction.DOWN  to IO.NONE,
            Direction.NORTH to IO.NONE,
            Direction.EAST  to IO.NONE,
            Direction.SOUTH to IO.NONE,
            Direction.WEST  to IO.NONE
        )
    )

    override fun tick() {
        componentEnergyContainer.tick()
        componentMechanicalDevice.tick()
        componentEnergyContainer.storedEnergy += (componentMechanicalDevice.rpm / 10)
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        return componentEnergyContainer.toTag(tag)
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        componentEnergyContainer.fromTag(state, tag)
    }

    override fun refresh() {
        markDirty()
    }
}