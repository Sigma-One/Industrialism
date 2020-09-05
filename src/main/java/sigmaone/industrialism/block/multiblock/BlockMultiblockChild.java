package sigmaone.industrialism.block.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class BlockMultiblockChild extends Block implements BlockEntityProvider {
    public BlockMultiblockChild(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new BlockEntityMultiblockChild();
    }
}
