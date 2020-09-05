package sigmaone.industrialism.block.wiring;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class BlockWireConnectorT1 extends BlockWireNode implements BlockEntityProvider {
    public BlockWireConnectorT1(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new BlockEntityWireNode();
    }
}
