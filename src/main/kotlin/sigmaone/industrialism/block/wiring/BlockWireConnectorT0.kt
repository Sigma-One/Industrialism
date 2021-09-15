package sigmaone.industrialism.block.wiring

import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import sigmaone.industrialism.Industrialism


class BlockWireConnectorT0(settings: Settings?) : BlockWireNode(settings!!, 5), BlockEntityProvider {
    override fun createBlockEntity(blockPos: BlockPos?, blockState: BlockState?): BlockEntity {
        return BlockEntityWireConnectorT0(blockPos, blockState)
    }

    override fun <T : BlockEntity?> getTicker(
        blockWorld: World?,
        blockState: BlockState?,
        type: BlockEntityType<T>?
    ): BlockEntityTicker<T>? {
        return checkType(
            type, Industrialism.CONNECTOR_T0
        ) { world: World, pos: BlockPos, state: BlockState, entity: BlockEntityWireConnectorT0 ->
            BlockEntityEnergyWireNode.tick(
                entity,
                pos,
                state,
                world
            )
        }
    }

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }
}