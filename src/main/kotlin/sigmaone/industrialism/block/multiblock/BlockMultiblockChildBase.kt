package sigmaone.industrialism.block.multiblock

import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

class BlockMultiblockChildBase(settings: Settings?) : Block(settings), BlockEntityProvider {
    var shape: VoxelShape = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
    override fun createBlockEntity(world: BlockView): BlockEntity? {
        return BlockEntityMultiblockChildBase()
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return shape
    }

    override fun getCollisionShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return shape
    }
}