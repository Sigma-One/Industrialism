package sigmaone.industrialism.block.multiblock

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.util.math.BlockPos
import sigmaone.industrialism.util.MultiblockHelper

abstract class BlockEntityMultiblockRoot(
    type: BlockEntityType<*>?,
    blockPos: BlockPos?,
    blockState: BlockState?
) : BlockEntity(type, blockPos, blockState) {
    var children: ArrayList<BlockPos> = ArrayList()

    fun addChild(pos: BlockPos) {
        children.add(pos)
        markDirty()
    }

    fun disassemble() {
        MultiblockHelper.destroyMultiblock(world!!, pos!!, world!!.getBlockState(pos!!).block as BlockMultiblockRoot)
    }

    override fun readNbt(tag: NbtCompound?) {
        super.readNbt(tag)
        val childrenTag: NbtCompound = tag!!.getCompound("children")
        for (i in 0..childrenTag.getInt("amount")) {
            children.add(NbtHelper.toBlockPos(childrenTag.getCompound(i.toString())))
        }
    }

    override fun writeNbt(tag: NbtCompound?): NbtCompound {
        super.writeNbt(tag)
        val childrenTag = NbtCompound()
        for ((i, child) in children.withIndex()) {
            childrenTag.put(i.toString(), NbtHelper.fromBlockPos(child))
        }
        childrenTag.putInt("amount", children.size)
        tag!!.put("children", childrenTag)
        return tag
    }
}