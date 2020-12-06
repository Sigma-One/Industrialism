package sigmaone.industrialism.block.multiblock

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.TransparentBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

class BlockMultiblockChild(settings: Settings?) : TransparentBlock(settings), BlockEntityProvider {
    var shape: VoxelShape = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
    override fun createBlockEntity(world: BlockView): BlockEntity? {
        return BlockEntityMultiblockChild()
    }

    override fun getVisualShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape? {
        return VoxelShapes.empty()
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return shape
    }

    override fun isTranslucent(state: BlockState, world: BlockView, pos: BlockPos): Boolean {
        return true
    }

    @Environment(EnvType.CLIENT)
    override fun getAmbientOcclusionLightLevel(state: BlockState, world: BlockView, pos: BlockPos): Float {
        return 1.0f
    }

    override fun getCollisionShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return shape
    }

    override fun onBreak(world: World?, pos: BlockPos?, state: BlockState?, player: PlayerEntity?) {
        val parent = world!!.getBlockEntity((world.getBlockEntity(pos!!) as BlockEntityMultiblockChild).parent)
        if (parent != null) {
            (parent as BlockEntityMultiblockRoot).disassemble()
        }
        super.onBreak(world, pos, state, player)
    }

    override fun onUse(state: BlockState?, world: World?, pos: BlockPos?, player: PlayerEntity?, hand: Hand?, hit: BlockHitResult?): ActionResult {
        val parent = world!!.getBlockState((world.getBlockEntity(pos!!) as BlockEntityMultiblockChild).parent)
        return if (parent != null) {
            parent.block.onUse(parent, world, (world.getBlockEntity(pos) as BlockEntityMultiblockChild).parent, player, hand, hit)
            ActionResult.SUCCESS
        }
        else {
            super.onUse(state, world, pos, player, hand, hit)
        }
    }
}