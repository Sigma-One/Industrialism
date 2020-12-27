package sigmaone.industrialism.block.machine

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Tickable
import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.block.IBlockEntityRefreshable
import sigmaone.industrialism.component.mechanical.ComponentMechanicalDevice
import sigmaone.industrialism.component.mechanical.IComponentMechanicalDevice
import sigmaone.industrialism.util.IO

class BlockEntityCrankHandle:
    BlockEntity(Industrialism.CRANK_HANDLE),
    IBlockEntityRefreshable,
    IComponentMechanicalDevice,
    BlockEntityClientSerializable,
    Tickable
{
    override fun refresh() {
        markDirty()
        if (world != null && !world!!.isClient) {
            sync()
        }
    }

    override val componentMechanicalDevice = ComponentMechanicalDevice(
        this,
        1.0,
        999.0,
        hashMapOf(
            Direction.UP    to IO.INPUT,
            Direction.DOWN  to IO.INPUT,
            Direction.NORTH to IO.INPUT,
            Direction.EAST  to IO.INPUT,
            Direction.SOUTH to IO.INPUT,
            Direction.WEST  to IO.INPUT
        )
    )

    override fun fromClientTag(tag: CompoundTag?) {
        componentMechanicalDevice.fromClientTag(tag)
    }

    override fun toClientTag(tag: CompoundTag?): CompoundTag {
        return componentMechanicalDevice.toClientTag(tag)
    }

    override fun fromTag(state: BlockState?, tag: CompoundTag?) {
        componentMechanicalDevice.fromClientTag(tag)
    }

    override fun toTag(tag: CompoundTag?): CompoundTag {
        return componentMechanicalDevice.toClientTag(tag)
    }

    override fun tick() {
        componentMechanicalDevice.tick()
    }
}