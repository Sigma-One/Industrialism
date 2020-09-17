package sigmaone.industrialism.block.multiblock

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.BlockPos
import sigmaone.industrialism.Industrialism

class BlockEntityMultiblockChildBase : BlockEntity(Industrialism.MULTIBLOCK_CHILD) {
    @JvmField protected var parent: BlockEntityMultiblockRootBase? = null
    var posInLayout: IntArray? = null
    var parentLocation: IntArray? = null
        private set

    fun setParent(parent: BlockEntityMultiblockRootBase) {
        this.parent = parent
        parentLocation = intArrayOf(parent.pos.x, parent.pos.y, parent.pos.z)
        parent.addChild(this)
        markDirty()
    }

    fun getParent(): BlockEntityMultiblockRootBase? {
        if (parent == null && getWorld() != null) {
            parent = getWorld()!!.getBlockEntity(BlockPos(parentLocation!!.get(0), parentLocation!![1], parentLocation!![2])) as BlockEntityMultiblockRootBase?
            markDirty()
        }
        return parent
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)
        if (getParent() != null) {
            tag.putIntArray("parent", intArrayOf(getParent()!!.pos.x, getParent()!!.pos.y, getParent()!!.pos.z))
        }
        return tag
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)
        val pos = tag.getIntArray("parent")
        if (pos.size != 0) {
            parentLocation = pos
        }
    }
}