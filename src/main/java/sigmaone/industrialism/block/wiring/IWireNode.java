package sigmaone.industrialism.block.wiring;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public interface IWireNode {
    /*
     * Add a new connection
     *
     * @param pos Position of other end of connection
     *
     * @return Whether the connection was successful or not
     */
    boolean addConnection(BlockPos pos);
    /*
     * Remove a connection
     *
     * @param pos    Position of other end of connection
     */
    void removeConnection(BlockPos pos);
    /*
     * Get a list of connections
     *
     * @return An ArrayList of BlockPoses representing locations of connected nodes
     */
     ArrayList<BlockPos> getConnections();
}
