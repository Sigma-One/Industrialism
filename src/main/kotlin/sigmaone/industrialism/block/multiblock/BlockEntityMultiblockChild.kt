package sigmaone.industrialism.block.multiblock

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.util.math.BlockPos
import sigmaone.industrialism.Industrialism

class BlockEntityMultiblockChild(
    blockPos: BlockPos?,
    blockState: BlockState?
) : BlockEntity(Industrialism.MULTIBLOCK_CHILD, blockPos, blockState) {
    var parent: BlockPos? = null
    set(value) {
        field = value
        markDirty()
    }

    override fun readNbt(tag: NbtCompound?) {
        super.readNbt(tag)
        parent = NbtHelper.toBlockPos(tag!!.getCompound("parent"))
    }

    override fun writeNbt(tag: NbtCompound?): NbtCompound {
        super.writeNbt(tag)
        if (parent != null) {
            tag!!.put("parent", NbtHelper.fromBlockPos(parent))
        }
        return tag!!
    }
}