package sigmaone.industrialism.block.multiblock;

import net.minecraft.block.Block;
import net.minecraft.util.shape.VoxelShape;

public abstract class BlockMultiblockRootBase extends Block {
    public abstract Block[][][] getLayout();
    public abstract int[] getRootPos();
    public abstract VoxelShape[][][] getShape();

    public BlockMultiblockRootBase(Settings settings) {
        super(settings);
    }
}
