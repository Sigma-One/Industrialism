package sigmaone.industrialism.block.multiblock

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtHelper
import net.minecraft.util.math.BlockPos
import sigmaone.industrialism.Industrialism

class BlockEntityMultiblockChildBase : BlockEntity(Industrialism.MULTIBLOCK_CHILD) {
    var parent: BlockPos? = null
    set(value) {
        field = value
        markDirty()
    }

    override fun fromTag(state: BlockState?, tag: CompoundTag?) {
        super.fromTag(state, tag)
        parent = NbtHelper.toBlockPos(tag!!.getCompound("parent"))
    }

    override fun toTag(tag: CompoundTag?): CompoundTag {
        super.toTag(tag)
        if (parent != null) {
            tag!!.put("parent", NbtHelper.fromBlockPos(parent))
        }
        return tag!!
    }
}