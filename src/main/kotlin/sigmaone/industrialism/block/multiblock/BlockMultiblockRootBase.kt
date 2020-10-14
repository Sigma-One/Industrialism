package sigmaone.industrialism.block.multiblock

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import sigmaone.industrialism.util.MultiblockHelper

abstract class BlockMultiblockRootBase(settings: Settings?) : HorizontalFacingBlock(settings) {
    abstract val layout: Array<Array<Array<Block>>>
    abstract val rootPos: IntArray
    abstract val shape: Array<Array<Array<VoxelShape>>>
    override fun appendProperties(stateManager: StateManager.Builder<Block, BlockState>) {
        stateManager.add(Properties.HORIZONTAL_FACING)
    }

    override fun onBreak(world: World?, pos: BlockPos?, state: BlockState?, player: PlayerEntity?) {
        (world!!.getBlockEntity(pos) as BlockEntityMultiblockRootBase).disassemble()
        super.onBreak(world, pos, state, player)
    }

    init {
        defaultState = stateManager.defaultState.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
    }
}