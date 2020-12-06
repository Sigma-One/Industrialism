package sigmaone.industrialism.block.multiblock

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtHelper
import net.minecraft.util.math.BlockPos
import sigmaone.industrialism.util.MultiblockHelper
import kotlin.collections.ArrayList

abstract class BlockEntityMultiblockRoot(type: BlockEntityType<*>?) : BlockEntity(type) {
    var children: ArrayList<BlockPos> = ArrayList()

    fun addChild(pos: BlockPos) {
        children.add(pos)
        markDirty()
    }

    fun disassemble() {
        MultiblockHelper.destroyMultiblock(world!!, pos!!, world!!.getBlockState(pos!!).block as BlockMultiblockRoot)
    }

    override fun fromTag(state: BlockState?, tag: CompoundTag?) {
        super.fromTag(state, tag)
        val childrenTag: CompoundTag = tag!!.getCompound("children")
        for (i in 0..childrenTag.getInt("amount")) {
            children.add(NbtHelper.toBlockPos(childrenTag.getCompound(i.toString())))
        }
    }

    override fun toTag(tag: CompoundTag?): CompoundTag {
        super.toTag(tag)
        val childrenTag = CompoundTag()
        for ((i, child) in children.withIndex()) {
            childrenTag.put(i.toString(), NbtHelper.fromBlockPos(child))
        }
        childrenTag.putInt("amount", children.size)
        tag!!.put("children", childrenTag)
        return tag
    }
}