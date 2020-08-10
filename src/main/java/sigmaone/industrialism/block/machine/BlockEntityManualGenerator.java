package sigmaone.industrialism.block.machine;

import net.minecraft.util.math.Direction;
import sigmaone.industrialism.Industrialism;
import sigmaone.industrialism.block.BlockEntitySidedEnergyContainer;

import java.util.HashMap;

public class BlockEntityManualGenerator extends BlockEntitySidedEnergyContainer {

    private static HashMap<Direction, Industrialism.SideEnergyConfig> sideConfig = new HashMap<>();

    static {
        sideConfig.put(Direction.NORTH, Industrialism.SideEnergyConfig.OUTPUT);
        sideConfig.put(Direction.SOUTH, Industrialism.SideEnergyConfig.OUTPUT);
        sideConfig.put(Direction.EAST , Industrialism.SideEnergyConfig.OUTPUT);
        sideConfig.put(Direction.WEST , Industrialism.SideEnergyConfig.OUTPUT);
        sideConfig.put(Direction.UP   , Industrialism.SideEnergyConfig.NONE);
        sideConfig.put(Direction.DOWN  , Industrialism.SideEnergyConfig.NONE);
    }

    public BlockEntityManualGenerator() {
        super(Industrialism.MANUAL_GENERATOR, 100, sideConfig);
    }
}
