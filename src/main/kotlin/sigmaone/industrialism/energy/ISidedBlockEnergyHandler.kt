package sigmaone.industrialism.energy

import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.Direction
import java.util.*

interface ISidedBlockEnergyHandler {
    /*
     * Get a map of connected power-accepting neighbours
     *
     * @return Vector containing only sides where power transfer is possible
     */
    val acceptingNeighbours: Vector<Direction>

    /*
     * Get a neighbouring BlockEntity on a specific side
     *
     * @param dir Direction to get neighbour in
     *
     * @return Neighbouring BlockEntity if it exists, null otherwise
     */
    fun getNeighbour(dir: Direction?): BlockEntity?
}