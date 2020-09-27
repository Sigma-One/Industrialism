package sigmaone.industrialism.block.multiblock

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.BlockPos
import java.util.*

abstract class BlockEntityMultiblockRootBase(type: BlockEntityType<*>?) : BlockEntity(type) {
    var children: HashSet<BlockEntityMultiblockChildBase> = HashSet()

    fun disassemble() {
        for (child in children) {
            world!!.setBlockState(child.pos, Blocks.AIR.defaultState)
            world!!.setBlockState(pos, Blocks.AIR.defaultState)
        }
    }

    override fun fromTag(state: BlockState?, tag: CompoundTag?) {
        super.fromTag(state, tag)
        val childrenTag: CompoundTag = tag!!.get("children") as CompoundTag
        for (i in 0..childrenTag.getInt("amount")) {
            val posArray = childrenTag.getIntArray(i.toString())
            children.add(world!!.getBlockEntity(BlockPos(posArray[0], posArray[1], posArray[2])) as BlockEntityMultiblockChildBase)
        }
    }

    override fun toTag(tag: CompoundTag?): CompoundTag {
        val childrenTag = CompoundTag()
        for ((i, child) in children.withIndex()) {
            childrenTag.putIntArray(i.toString(), intArrayOf(child.pos.x, child.pos.y, child.pos.z))
        }
        childrenTag.putInt("amount", children.size)
        tag!!.put("children", childrenTag)
        return tag
    }
}