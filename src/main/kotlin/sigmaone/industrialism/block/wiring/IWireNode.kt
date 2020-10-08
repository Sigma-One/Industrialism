package sigmaone.industrialism.block.wiring

import net.minecraft.util.math.BlockPos
import java.util.*
import kotlin.collections.HashSet

interface IWireNode {
    /*
     * Add a new connection
     *
     * @param pos Position of other end of connection
     *
     * @return Whether the connection was successful or not
     */
    fun addConnection(pos: BlockPos): Boolean

    /*
     * Remove a connection
     *
     * @param pos    Position of other end of connection
     */
    fun removeConnection(pos: BlockPos)

    /*
     * Get a list of connections
     *
     * @return An ArrayList of BlockPoses representing locations of connected nodes
     */
    val connections: HashSet<BlockPos>
}