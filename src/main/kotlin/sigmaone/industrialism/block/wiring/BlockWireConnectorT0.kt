package sigmaone.industrialism.block.wiring

import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.world.BlockView

class BlockWireConnectorT0(settings: Settings?) : BlockWireNode(settings!!, 5), BlockEntityProvider {
    override fun createBlockEntity(world: BlockView): BlockEntity? {
        return BlockEntityWireNode()
    }
}