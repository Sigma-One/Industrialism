package sigmaone.industrialism.block.machine;

import net.minecraft.util.math.Direction;
import sigmaone.industrialism.Industrialism;
import sigmaone.industrialism.block.BlockEntitySidedEnergyContainer;

import java.util.HashMap;

public class BlockEntityManualGenerator extends BlockEntitySidedEnergyContainer {

    private static HashMap<Direction, Industrialism.InputConfig> sideConfig = new HashMap<>();

    static {
        sideConfig.put(Direction.NORTH, Industrialism.InputConfig.OUTPUT);
        sideConfig.put(Direction.SOUTH, Industrialism.InputConfig.OUTPUT);
        sideConfig.put(Direction.EAST , Industrialism.InputConfig.OUTPUT);
        sideConfig.put(Direction.WEST , Industrialism.InputConfig.OUTPUT);
        sideConfig.put(Direction.UP   , Industrialism.InputConfig.NONE);
        sideConfig.put(Direction.DOWN  , Industrialism.InputConfig.NONE);
    }

    public BlockEntityManualGenerator() {
        super(Industrialism.MANUAL_GENERATOR, 100, sideConfig);
    }
}
