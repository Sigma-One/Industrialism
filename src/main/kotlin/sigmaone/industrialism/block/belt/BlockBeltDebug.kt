package sigmaone.industrialism.block.belt

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

class BlockBeltDebug(settings: Settings) : HorizontalFacingBlock(settings.nonOpaque()) {
    override fun appendProperties(stateBuilder: StateManager.Builder<Block, BlockState>) {
        stateBuilder.add(Properties.HORIZONTAL_FACING)
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, (1/16.0)*3.0, 1.0)
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