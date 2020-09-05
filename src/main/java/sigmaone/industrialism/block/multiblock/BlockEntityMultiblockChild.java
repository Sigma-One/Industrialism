package sigmaone.industrialism.block.multiblock;

import net.minecraft.block.entity.BlockEntity;
import sigmaone.industrialism.Industrialism;

public class BlockEntityMultiblockChild extends BlockEntity {
    BlockEntityMultiblockRootBase parent;

    public BlockEntityMultiblockChild(/*BlockEntityType<?> type, BlockEntityMultiblockRootBase parent*/) {
        super(Industrialism.MULTIBLOCK_CHILD);
        //this.parent = parent;
    }
}
