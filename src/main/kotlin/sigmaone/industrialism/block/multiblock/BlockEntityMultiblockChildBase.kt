package sigmaone.industrialism.block.multiblock

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.BlockPos
import sigmaone.industrialism.Industrialism

class BlockEntityMultiblockChildBase : BlockEntity(Industrialism.MULTIBLOCK_CHILD) {
    var parent: BlockEntityMultiblockRootBase? = null
    set(value) {
        field = value
        value!!.children.add(this)
        markDirty()
    }
}