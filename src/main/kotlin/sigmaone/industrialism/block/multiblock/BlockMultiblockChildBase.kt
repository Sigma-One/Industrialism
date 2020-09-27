package sigmaone.industrialism.block.multiblock

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.TransparentBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

class BlockMultiblockChildBase(settings: Settings?) : TransparentBlock(settings), BlockEntityProvider {
    var shape: VoxelShape = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
    override fun createBlockEntity(world: BlockView): BlockEntity? {
        return BlockEntityMultiblockChildBase()
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
}