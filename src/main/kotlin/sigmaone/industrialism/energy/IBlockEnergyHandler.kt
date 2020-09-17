package sigmaone.industrialism.energy

import net.minecraft.util.math.BlockPos

interface IBlockEnergyHandler {
    /*
     * Transfer energy to a block at position pos
     *
     * @param pos BlockPos of the recipient
     */
    fun sendEnergy(pos: BlockPos?, amount: Float)
}