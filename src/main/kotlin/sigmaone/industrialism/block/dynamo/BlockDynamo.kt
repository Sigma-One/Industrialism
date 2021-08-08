package sigmaone.industrialism.block.dynamo

import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.FacingBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import sigmaone.industrialism.util.IO

class BlockDynamo(settings: Settings?) : FacingBlock(settings), BlockEntityProvider {
    override fun onPlaced(
        world: World?,
        pos: BlockPos?,
        state: BlockState?,
        placer: LivingEntity?,
        itemStack: ItemStack?
    ) {
        val entity = world!!.getBlockEntity(pos!!)
        val facing = state!!.get(Properties.FACING)
        if (entity is BlockEntityDynamo) {
            entity.componentMechanicalDevice.sideConfig[facing] = IO.INPUT
            entity.componentEnergyContainer.sideConfig[facing] = IO.NONE
        }
        super.onPlaced(world, pos, state, placer, itemStack)
    }

    override fun createBlockEntity(blockPos: BlockPos?, blockState: BlockState?): BlockEntity {
        return BlockEntityDynamo(blockPos, blockState)
    }

    override fun appendProperties(stateBuilder: StateManager.Builder<Block, BlockState>) {
        stateBuilder.add(Properties.FACING)
    }

    override fun getPlacementState(context: ItemPlacementContext): BlockState? {
        return defaultState.with(FACING, context.side)
    }
}