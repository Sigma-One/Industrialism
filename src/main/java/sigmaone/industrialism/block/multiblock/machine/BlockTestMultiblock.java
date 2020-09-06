package sigmaone.industrialism.block.multiblock.machine;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import sigmaone.industrialism.block.multiblock.BlockMultiblockRootBase;

public class BlockTestMultiblock extends BlockMultiblockRootBase implements BlockEntityProvider {

    @Override
    public Block[][][] getLayout() {
        return new Block[][][] {
                { // layer
                        { Blocks.COBBLESTONE, Blocks.COBBLESTONE }, // row
                        { Blocks.COBBLESTONE, Blocks.COBBLESTONE }  // row
                }, // \layer
                { // layer
                        { Blocks.COBBLESTONE, Blocks.COBBLESTONE }, // row
                        { Blocks.COBBLESTONE, Blocks.COBBLESTONE }  // row
                } // \layer
        };
    }

    @Override
    public int[] getRootPos() {
        return new int[] {0, 0, 0};
    }

    @Override
    public VoxelShape[][][] getShape() {
        return new VoxelShape[][][] {
                {
                        { VoxelShapes.fullCube(), VoxelShapes.fullCube() },
                        { VoxelShapes.fullCube(), VoxelShapes.fullCube() },
                },
                {
                        { VoxelShapes.fullCube(), VoxelShapes.fullCube() },
                        { VoxelShapes.fullCube(), VoxelShapes.fullCube() }
                }
        };
    }

    public BlockTestMultiblock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new BlockEntityTestMultiblock();
    }
}
