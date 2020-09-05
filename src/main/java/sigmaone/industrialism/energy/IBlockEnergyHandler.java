package sigmaone.industrialism.energy;

import net.minecraft.util.math.BlockPos;

public interface IBlockEnergyHandler {
    /*
     * Transfer energy to a block at position pos
     *
     * @param pos BlockPos of the recipient
     */
    void sendEnergy(BlockPos pos, float amount);
}
