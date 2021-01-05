package sigmaone.industrialism.block.multiblock.blastfurnace

import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.block.multiblock.BlockMultiblockRoot

class BlockBlastFurnaceMultiblock(settings: Settings?) : BlockMultiblockRoot(settings), BlockEntityProvider {
    override val layout: Array<Array<Array<Block>>>
        get() = arrayOf(
            arrayOf(
                arrayOf(Industrialism.BRACED_FIRE_BRICKS, Industrialism.BRACED_FIRE_BRICKS, Industrialism.BRACED_FIRE_BRICKS),
                arrayOf(Industrialism.BRACED_FIRE_BRICKS, Industrialism.BRACED_FIRE_BRICKS, Industrialism.BRACED_FIRE_BRICKS),
                arrayOf(Industrialism.BRACED_FIRE_BRICKS, Industrialism.BRACED_FIRE_BRICKS, Industrialism.BRACED_FIRE_BRICKS)
            ),
            arrayOf(
                arrayOf(Industrialism.BRACED_FIRE_BRICKS, Blocks.NETHER_BRICKS, Industrialism.BRACED_FIRE_BRICKS),
                arrayOf(Blocks.IRON_BARS, Blocks.AIR, Blocks.NETHER_BRICKS),
                arrayOf(Industrialism.BRACED_FIRE_BRICKS, Blocks.NETHER_BRICKS, Industrialism.BRACED_FIRE_BRICKS)
            ),
            arrayOf(
                arrayOf(Industrialism.BRACED_FIRE_BRICKS, Industrialism.BRACED_FIRE_BRICKS, Industrialism.BRACED_FIRE_BRICKS),
                arrayOf(Industrialism.BRACED_FIRE_BRICKS, Industrialism.BRACED_FIRE_BRICKS, Industrialism.BRACED_FIRE_BRICKS),
                arrayOf(Industrialism.BRACED_FIRE_BRICKS, Industrialism.BRACED_FIRE_BRICKS, Industrialism.BRACED_FIRE_BRICKS)
            )
        )

    override val rootPos: IntArray
        get() = intArrayOf(1, 1, 0)

    override val shape: Array<Array<Array<VoxelShape>>>
        get() = arrayOf(
            arrayOf(
                arrayOf(VoxelShapes.fullCube(), VoxelShapes.fullCube(), VoxelShapes.fullCube()),
                arrayOf(VoxelShapes.fullCube(), VoxelShapes.fullCube(), VoxelShapes.fullCube()),
                arrayOf(VoxelShapes.fullCube(), VoxelShapes.fullCube(), VoxelShapes.fullCube())
            ),
            arrayOf(
                arrayOf(VoxelShapes.fullCube(), VoxelShapes.fullCube(), VoxelShapes.fullCube()),
                arrayOf(VoxelShapes.fullCube(), VoxelShapes.fullCube(), VoxelShapes.fullCube()),
                arrayOf(VoxelShapes.fullCube(), VoxelShapes.fullCube(), VoxelShapes.fullCube())
            ),
            arrayOf(
                arrayOf(VoxelShapes.fullCube(), VoxelShapes.fullCube(), VoxelShapes.fullCube()),
                arrayOf(VoxelShapes.fullCube(), VoxelShapes.fullCube(), VoxelShapes.fullCube()),
                arrayOf(VoxelShapes.fullCube(), VoxelShapes.fullCube(), VoxelShapes.fullCube())
            )
        )

    override fun createBlockEntity(world: BlockView): BlockEntity {
        return BlockEntityBlastFurnaceMultiblock()
    }

    override fun appendProperties(stateManager: StateManager.Builder<Block, BlockState>) {
        stateManager.add(Properties.HORIZONTAL_FACING).add(Properties.LIT)
    }

    override fun onUse(state: BlockState?, world: World?, pos: BlockPos?, player: PlayerEntity?, hand: Hand?, hit: BlockHitResult?): ActionResult {
        player!!.openHandledScreen(state!!.createScreenHandlerFactory(world, pos))
        return ActionResult.SUCCESS
    }

    override fun createScreenHandlerFactory(state: BlockState?, world: World, pos: BlockPos?): NamedScreenHandlerFactory? {
        val blockEntity = world.getBlockEntity(pos)
        return if (blockEntity is NamedScreenHandlerFactory) blockEntity else null
    }

    init {
        defaultState = stateManager.defaultState.with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(Properties.LIT, false)
    }
}