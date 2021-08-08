package sigmaone.industrialism.block.wiring

import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos

class BlockWireConnectorT0(settings: Settings?) : BlockWireNode(settings!!, 5), BlockEntityProvider {
    override fun createBlockEntity(blockPos: BlockPos?, blockState: BlockState?): BlockEntity {
        return BlockEntityWireConnectorT0(blockPos, blockState)
    }
}