package sigmaone.industrialism.block.multiblock

import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import java.util.*

abstract class BlockEntityMultiblockRootBase(type: BlockEntityType<*>?) : BlockEntity(type) {
    protected var children: HashSet<BlockEntityMultiblockChildBase> = HashSet()

    fun addChild(child: BlockEntityMultiblockChildBase) {
        children.add(child)
    }
}