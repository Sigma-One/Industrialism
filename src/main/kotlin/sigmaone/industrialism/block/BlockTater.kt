package sigmaone.industrialism.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.ShapeContext
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

class BlockTater(settings: Settings) : HorizontalFacingBlock(settings.nonOpaque()) {
    override fun appendProperties(stateBuilder: StateManager.Builder<Block, BlockState>) {
        stateBuilder.add(Properties.HORIZONTAL_FACING)
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return VoxelShapes.cuboid(1f / 16f * 6f.toDouble(), 0.0, 1f / 16f * 6f.toDouble(), 1f / 16f * 10f.toDouble(), 1f / 16f * 7f.toDouble(), 1f / 16f * 10f.toDouble())
    }

    override fun getCollisionShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return getOutlineShape(state, world, pos, context)
    }

    override fun getPlacementState(context: ItemPlacementContext): BlockState? {
        return defaultState.with(Properties.HORIZONTAL_FACING, context.playerFacing)
    }

    init {
        defaultState = getStateManager().defaultState.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
    }
}