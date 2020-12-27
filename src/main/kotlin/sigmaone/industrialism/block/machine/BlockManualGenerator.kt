package sigmaone.industrialism.block.machine

import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.world.BlockView

class BlockManualGenerator(settings: Settings?) : Block(settings), BlockEntityProvider {
    override fun createBlockEntity(world: BlockView): BlockEntity? {
        return BlockEntityManualGenerator()
    }
}