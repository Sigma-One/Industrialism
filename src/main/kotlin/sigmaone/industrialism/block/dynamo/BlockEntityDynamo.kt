package sigmaone.industrialism.block.dynamo

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.block.IBlockEntityRefreshable
import sigmaone.industrialism.component.energy.ComponentEnergyContainer
import sigmaone.industrialism.component.energy.IComponentEnergyContainer
import sigmaone.industrialism.component.mechanical.ComponentMechanicalDevice
import sigmaone.industrialism.component.mechanical.IComponentMechanicalDevice
import sigmaone.industrialism.util.IO
import team.reborn.energy.EnergyTier

class BlockEntityDynamo(blockPos: BlockPos?, blockState: BlockState?) :
    BlockEntity(Industrialism.DYNAMO, blockPos, blockState),
    IComponentEnergyContainer,
    IComponentMechanicalDevice,
    BlockEntityClientSerializable,
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
            Direction.UP    to IO.OUTPUT,
            Direction.DOWN  to IO.OUTPUT
        )
    )

    override val componentMechanicalDevice = ComponentMechanicalDevice(
        this,
        1.0,
        999.0,
        hashMapOf(
            Direction.UP    to IO.NONE,
            Direction.DOWN  to IO.NONE,
            Direction.NORTH to IO.NONE,
            Direction.EAST  to IO.NONE,
            Direction.SOUTH to IO.NONE,
            Direction.WEST  to IO.NONE
        )
    )

    fun tick() {
        componentEnergyContainer.tick()
        componentMechanicalDevice.tick()
        componentEnergyContainer.storedEnergy += (componentMechanicalDevice.rpm / 5)
    }

    override fun writeNbt(tag: NbtCompound): NbtCompound {
        super.writeNbt(tag)
        var tag = componentMechanicalDevice.writeNbt(tag)
        tag = componentEnergyContainer.writeNbt(tag)
        return tag
    }

    override fun readNbt(tag: NbtCompound) {
        super.readNbt(tag)
        componentMechanicalDevice.readNbt(tag)
        componentEnergyContainer.readNbt(tag)
    }

    override fun refresh() {
        markDirty()
    }

    override fun fromClientTag(tag: NbtCompound?) {
        componentMechanicalDevice.fromClientTag(tag)
    }

    override fun toClientTag(tag: NbtCompound?): NbtCompound {
        return componentMechanicalDevice.toClientTag(tag)
    }
}