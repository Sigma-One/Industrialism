package sigmaone.industrialism.block.multiblock

import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import java.util.*

abstract class BlockEntityMultiblockRootBase(type: BlockEntityType<*>?) : BlockEntity(type) {
    protected var children: HashSet<BlockEntityMultiblockChildBase> = HashSet()
    private var block: BlockMultiblockRootBase = getWorld()!!.getBlockState(getPos()).block as BlockMultiblockRootBase

    fun addChild(child: BlockEntityMultiblockChildBase) {
        children.add(child)
    }

    fun disassemble() {
        if (getWorld() != null) {
            for (child in children) {
                if (getWorld()!!.getBlockState(child.pos) != null) {
                    getWorld()!!.setBlockState(child.pos, block.layout[child.posInLayout!![1]][child.posInLayout!![0]][child.posInLayout!![2]].defaultState)
                    getWorld()!!.removeBlockEntity(child.pos)
                }
            }
            if (getWorld() != null) {
                getWorld()!!.setBlockState(getPos(), block.layout[block.rootPos!![1]][block.rootPos!![0]][block.rootPos!![2]].defaultState)
                getWorld()!!.removeBlockEntity(getPos())
            }
        }
    }
}