package sigmaone.industrialism.block.crankhandle

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.block.IBlockEntityRefreshable
import sigmaone.industrialism.component.mechanical.ComponentMechanicalDevice
import sigmaone.industrialism.component.mechanical.IComponentMechanicalDevice
import sigmaone.industrialism.util.IO

class BlockEntityCrankHandle(blockPos: BlockPos?, blockState: BlockState?) :
    BlockEntity(Industrialism.CRANK_HANDLE, blockPos, blockState),
    IBlockEntityRefreshable,
    IComponentMechanicalDevice<BlockEntityCrankHandle>,
    BlockEntityClientSerializable
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
        30.0,
        hashMapOf(
            Direction.UP    to IO.NONE,
            Direction.DOWN  to IO.NONE,
            Direction.NORTH to IO.NONE,
            Direction.EAST  to IO.NONE,
            Direction.SOUTH to IO.NONE,
            Direction.WEST  to IO.NONE
        )
    )

    override fun fromClientTag(tag: NbtCompound?) {
        componentMechanicalDevice.fromClientTag(tag)
    }

    override fun toClientTag(tag: NbtCompound?): NbtCompound {
        return componentMechanicalDevice.toClientTag(tag)
    }

    override fun readNbt(tag: NbtCompound?) {
        super.readNbt(tag)
        componentMechanicalDevice.fromClientTag(tag)
    }

    override fun writeNbt(tag: NbtCompound?): NbtCompound {
        super.writeNbt(tag)
        return componentMechanicalDevice.toClientTag(tag)
    }

    companion object {
        fun tick(entity: BlockEntityCrankHandle) {
            entity.componentMechanicalDevice.tick()
        }
    }
}