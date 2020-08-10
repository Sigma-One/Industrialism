package sigmaone.industrialism.energy;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import sigmaone.industrialism.Industrialism;

import java.util.HashMap;
import java.util.Vector;

public interface IBlockEnergyHandler {
    /*
     * Transfer energy to a block at position pos
     *
     * @param pos BlockPos of the recipient
     */
    void sendEnergy(BlockPos pos);
}
