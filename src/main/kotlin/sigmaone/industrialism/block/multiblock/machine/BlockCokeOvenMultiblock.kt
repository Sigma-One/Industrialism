package sigmaone.industrialism.block.multiblock.machine

import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import sigmaone.industrialism.block.multiblock.BlockMultiblockRootBase

class BlockCokeOvenMultiblock(settings: Settings?) : BlockMultiblockRootBase(settings), BlockEntityProvider {
    override val layout: Array<Array<Array<Block>>>
        get() = arrayOf(arrayOf(arrayOf(Blocks.BRICKS, Blocks.BRICKS, Blocks.BRICKS), arrayOf(Blocks.BRICKS, Blocks.BRICKS, Blocks.BRICKS), arrayOf(Blocks.BRICKS, Blocks.BRICKS, Blocks.BRICKS)), arrayOf(arrayOf(Blocks.BRICKS, Blocks.BRICKS, Blocks.BRICKS), arrayOf(Blocks.BRICKS, Blocks.AIR, Blocks.BRICKS), arrayOf(Blocks.BRICKS, Blocks.BRICKS, Blocks.BRICKS)), arrayOf(arrayOf(Blocks.BRICKS, Blocks.BRICKS, Blocks.BRICKS), arrayOf(Blocks.BRICKS, Blocks.BRICKS, Blocks.BRICKS), arrayOf(Blocks.BRICKS, Blocks.BRICKS, Blocks.BRICKS)))

    override val rootPos: IntArray
        get() = intArrayOf(1, 1, 0)

    override val shape: Array<Array<Array<VoxelShape>>>
        get() = arrayOf(arrayOf(arrayOf(VoxelShapes.fullCube(), VoxelShapes.fullCube(), VoxelShapes.fullCube()), arrayOf(VoxelShapes.fullCube(), VoxelShapes.fullCube(), VoxelShapes.fullCube()), arrayOf(VoxelShapes.fullCube(), VoxelShapes.fullCube(), VoxelShapes.fullCube())), arrayOf(arrayOf(VoxelShapes.fullCube(), VoxelShapes.fullCube(), VoxelShapes.fullCube()), arrayOf(VoxelShapes.fullCube(), VoxelShapes.fullCube(), VoxelShapes.fullCube()), arrayOf(VoxelShapes.fullCube(), VoxelShapes.fullCube(), VoxelShapes.fullCube())), arrayOf(arrayOf(VoxelShapes.fullCube(), VoxelShapes.fullCube(), VoxelShapes.fullCube()), arrayOf(VoxelShapes.fullCube(), VoxelShapes.fullCube(), VoxelShapes.fullCube()), arrayOf(VoxelShapes.fullCube(), VoxelShapes.fullCube(), VoxelShapes.fullCube())))

    override fun createBlockEntity(world: BlockView): BlockEntity? {
        // blockEntity.setLayout(this.getLayout());
        //blockEntity.setPosInLayout(this.getRootPos());
        return BlockEntityCokeOvenMultiblock()
    }
}