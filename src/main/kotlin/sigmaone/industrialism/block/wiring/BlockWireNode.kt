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

open class BlockWireNode(settings: Settings, val height: Int) : FacingBlock(settings.nonOpaque()) {

    override fun appendProperties(stateBuilder: StateManager.Builder<Block, BlockState>) {
        stateBuilder.add(STATE)
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

                    player.sendMessage(TranslatableText("popup." + Industrialism.MOD_ID + ".ioconfig.set.unsided", modeTranslated), true)
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