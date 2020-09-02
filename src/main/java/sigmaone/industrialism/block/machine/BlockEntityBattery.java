package sigmaone.industrialism.block.machine;

import net.minecraft.util.math.Direction;
import sigmaone.industrialism.Industrialism;
import sigmaone.industrialism.block.BlockEntitySidedEnergyContainer;
import sigmaone.industrialism.block.IConfigurable;

import java.util.HashMap;

public class BlockEntityBattery extends BlockEntitySidedEnergyContainer implements IConfigurable {
    private static HashMap<Direction, Industrialism.InputConfig> sideConfig = new HashMap<>();

    static {
        sideConfig.put(Direction.NORTH, Industrialism.InputConfig.NONE);
        sideConfig.put(Direction.SOUTH, Industrialism.InputConfig.NONE);
        sideConfig.put(Direction.EAST , Industrialism.InputConfig.NONE);
        sideConfig.put(Direction.WEST , Industrialism.InputConfig.NONE);
        sideConfig.put(Direction.UP   , Industrialism.InputConfig.NONE);
        sideConfig.put(Direction.DOWN  , Industrialism.InputConfig.NONE);
    }
    public BlockEntityBattery() {
        super(Industrialism.BATTERY, 800, sideConfig);
    }
}
