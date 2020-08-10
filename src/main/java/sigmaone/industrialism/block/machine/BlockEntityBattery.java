package sigmaone.industrialism.block.machine;

import net.minecraft.util.math.Direction;
import sigmaone.industrialism.Industrialism;
import sigmaone.industrialism.block.BlockEntitySidedEnergyContainer;

import java.util.HashMap;

public class BlockEntityBattery extends BlockEntitySidedEnergyContainer {
    private static HashMap<Direction, Industrialism.SideEnergyConfig> sideConfig = new HashMap<>();

    static {
        sideConfig.put(Direction.NORTH, Industrialism.SideEnergyConfig.NONE);
        sideConfig.put(Direction.SOUTH, Industrialism.SideEnergyConfig.NONE);
        sideConfig.put(Direction.EAST , Industrialism.SideEnergyConfig.NONE);
        sideConfig.put(Direction.WEST , Industrialism.SideEnergyConfig.NONE);
        sideConfig.put(Direction.UP   , Industrialism.SideEnergyConfig.NONE);
        sideConfig.put(Direction.DOWN  , Industrialism.SideEnergyConfig.NONE);
    }
    public BlockEntityBattery() {
        super(Industrialism.BATTERY, 800, sideConfig);
    }
}
