package sigmaone.industrialism.block.wiring

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.FacingBlock
import net.minecraft.block.ShapeContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import sigmaone.industrialism.component.wiring.IComponentWireNode

abstract class BlockWireNode(settings: Settings, val height: Int) : FacingBlock(settings.nonOpaque()) {

    override fun appendProperties(stateBuilder: StateManager.Builder<Block, BlockState>) {
        stateBuilder.add(sigmaone.industrialism.util.Properties.IO)
        stateBuilder.add(Properties.FACING)
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        val f116 = 1.0/16.0
        val midMin = f116*6
        val midMax = f116*10
        val heightD = f116*height
        return when (state.get(FACING)) {
            Direction.UP    -> VoxelShapes.cuboid(midMin, 0.0, midMin, midMax, heightD, midMax)
            Direction.DOWN  -> VoxelShapes.cuboid(midMin, 1.0, midMin, midMax, 1.0-heightD, midMax)
            Direction.NORTH -> VoxelShapes.cuboid(midMin, midMin, 1.0, midMax, midMax, 1.0-heightD)
            Direction.SOUTH -> VoxelShapes.cuboid(midMin, midMin, 0.0, midMax, midMax, heightD)
            Direction.EAST  -> VoxelShapes.cuboid(0.0, midMin, midMin, heightD, midMax, midMax)
            Direction.WEST  -> VoxelShapes.cuboid(1.0, midMin, midMin, 1.0-heightD, midMax, midMax)
            else -> throw IllegalStateException("Illegal side: " + state.get(FACING))
        }
    }

    override fun getCollisionShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return getOutlineShape(state, world, pos, context)
    }

    override fun getPlacementState(context: ItemPlacementContext): BlockState? {
        return defaultState.with(FACING, context.side)
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity is IComponentWireNode) {
            blockEntity.componentWireNode.removeAllConnections()
        }
        super.onBreak(world, pos, state, player)
    }

    init {
        defaultState = getStateManager().defaultState.with(sigmaone.industrialism.util.Properties.IO, 0).with(FACING, Direction.DOWN)
    }
}