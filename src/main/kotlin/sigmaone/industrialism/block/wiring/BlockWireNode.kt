package sigmaone.industrialism.block.wiring

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.FacingBlock
import net.minecraft.block.ShapeContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties
import net.minecraft.text.TranslatableText
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

open class BlockWireNode(settings: Settings) : FacingBlock(settings.nonOpaque()) {

    override fun appendProperties(stateBuilder: StateManager.Builder<Block, BlockState>) {
        stateBuilder.add(STATE)
        stateBuilder.add(Properties.FACING)
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return when (state.get(FACING)) {
            Direction.UP    -> VoxelShapes.cuboid(1f / 16f * 6f.toDouble(), 0.0, 1f / 16f * 6f.toDouble(), 1f / 16f * 10f.toDouble(), 1f / 16f * 8f.toDouble(), 1f / 16f * 10f.toDouble())
            Direction.DOWN  -> VoxelShapes.cuboid(1f / 16f * 6f.toDouble(), 1f / 16f * 8f.toDouble(), 1f / 16f * 6f.toDouble(), 1f / 16f * 10f.toDouble(), 1f / 16f * 16f.toDouble(), 1f / 16f * 10f.toDouble())
            Direction.NORTH -> VoxelShapes.cuboid(1f / 16f * 6f.toDouble(), 1f / 16f * 6f.toDouble(), 1f / 16f * 8f.toDouble(), 1f / 16f * 10f.toDouble(), 1f / 16f * 10f.toDouble(), 1.0)
            Direction.SOUTH -> VoxelShapes.cuboid(1f / 16f * 6f.toDouble(), 1f / 16f * 6f.toDouble(), 0.0, 1f / 16f * 10f.toDouble(), 1f / 16f * 10f.toDouble(), 1f / 16f * 8f.toDouble())
            Direction.EAST  -> VoxelShapes.cuboid(0.0, 1f / 16f * 6f.toDouble(), 1f / 16f * 6f.toDouble(), 1f / 16f * 8f.toDouble(), 1f / 16f * 10f.toDouble(), 1f / 16f * 10f.toDouble())
            Direction.WEST  -> VoxelShapes.cuboid(1.0, 1f / 16f * 6f.toDouble(), 1f / 16f * 6f.toDouble(), 1f / 16f * 8f.toDouble(), 1f / 16f * 10f.toDouble(), 1f / 16f * 10f.toDouble())
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
        val blockEntity = world.getBlockEntity(pos) as BlockEntityWireNode?
        blockEntity!!.removeAllConnections()
        super.onBreak(world, pos, state, player)
    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        val blockEntity = world.getBlockEntity(pos)
        if (!world.isClient) {
            if (blockEntity is BlockEntityWireNode) {
                if (player.getStackInHand(hand).item === Industrialism.SCREWDRIVER) {
                    blockEntity.cycleSideConfig(state.get(FACING).opposite)
                    blockEntity.cycleIOState()
                    world.setBlockState(pos, state.with(STATE, blockEntity.IOstate.ordinal))

                    val modeTranslated: String = TranslatableText("variable." + Industrialism.MOD_ID + ".ioconfig." + blockEntity
                            .sideConfig[state.get(FACING).opposite]
                            .toString().toLowerCase()).string

                    player.sendMessage(TranslatableText("popup." + Industrialism.MOD_ID + ".ioconfig.set_noside", modeTranslated), true)
                    return ActionResult.SUCCESS
                }
            }
        }
        return ActionResult.PASS
    }

    companion object {
        val STATE: IntProperty = IntProperty.of("state", 0, 2)
    }

    init {
        defaultState = getStateManager().defaultState.with(STATE, 0).with(FACING, Direction.DOWN)
    }
}