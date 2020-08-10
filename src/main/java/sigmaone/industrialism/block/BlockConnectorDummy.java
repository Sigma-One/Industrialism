package sigmaone.industrialism.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public class BlockConnectorDummy extends FacingBlock {
    private static final IntProperty STATE = IntProperty.of("state", 0, 2);
    public BlockConnectorDummy(Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(Properties.FACING, Direction.DOWN).with(STATE, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(STATE);
        stateBuilder.add(Properties.FACING);
    }


}
