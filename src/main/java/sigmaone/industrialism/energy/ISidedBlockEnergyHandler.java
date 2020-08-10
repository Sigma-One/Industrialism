package sigmaone.industrialism.energy;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Direction;

import java.util.Vector;

public interface ISidedBlockEnergyHandler {
    /*
     * Get a map of connected power-accepting neighbours
     *
     * @return Vector containing only sides where power transfer is possible
     */
    Vector<Direction> getAcceptingNeighbours();
    /*
     * Get a neighbouring BlockEntity on a specific side
     *
     * @param dir Direction to get neighbour in
     *
     * @return Neighbouring BlockEntity if it exists, null otherwise
     */
    BlockEntity getNeighbour(Direction dir);
}
