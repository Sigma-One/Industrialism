package sigmaone.industrialism.block.crankhandle

import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import sigmaone.industrialism.component.mechanical.IComponentMechanicalDevice
import sigmaone.industrialism.util.IO

class BlockCrankHandle(settings: Settings?) : FacingBlock(settings), BlockEntityProvider {
    override fun createBlockEntity(blockPos: BlockPos?, blockState: BlockState?): BlockEntity {
        return BlockEntityCrankHandle(blockPos, blockState)
    }

    override fun onPlaced(
        world: World?,
        pos: BlockPos?,
        state: BlockState?,
        placer: LivingEntity?,
        itemStack: ItemStack?
    ) {
        val entity = world!!.getBlockEntity(pos!!)
        val facing = state!!.get(Properties.FACING)
        if (entity is BlockEntityCrankHandle) {
            entity.componentMechanicalDevice.sideConfig[facing.opposite] = IO.OUTPUT
        }
        super.onPlaced(world, pos, state, placer, itemStack)
    }

    override fun onUse(
        state: BlockState?,
        world: World?,
        pos: BlockPos?,
        player: PlayerEntity?,
        hand: Hand?,
        hit: BlockHitResult?
    ): ActionResult {
        val entity = world!!.getBlockEntity(pos)
        if (entity is IComponentMechanicalDevice) {
            entity.componentMechanicalDevice.rpm += 10.0
            if (entity.componentMechanicalDevice.rpm > entity.componentMechanicalDevice.maxRpm) {
                entity.componentMechanicalDevice.rpm = entity.componentMechanicalDevice.maxRpm
            }
        }
        return ActionResult.SUCCESS
    }

    override fun getCollisionShape(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape {
        return VoxelShapes.empty()
    }

    override fun getOutlineShape(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape {
        return VoxelShapes.cuboid(
            (1.0/16)*4.0,  (1.0/16)*0.0,  (1.0/16)*4.0,
            (1.0/16)*12.0, (1.0/16)*10.0, (1.0/16)*12.0
        )
    }

    override fun appendProperties(stateBuilder: StateManager.Builder<Block, BlockState>) {
        stateBuilder.add(Properties.FACING)
    }

    override fun getPlacementState(context: ItemPlacementContext): BlockState? {
        return defaultState.with(FACING, context.side)
    }
}