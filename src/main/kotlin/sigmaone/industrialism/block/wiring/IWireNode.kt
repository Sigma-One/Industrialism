package sigmaone.industrialism.block.wiring

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import sigmaone.industrialism.energy.WireConnection
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

interface IWireNode {
    /**
     * Add a new connection
     *
     * @param pos Position of other end of connection
     *
     * @return Whether the connection was successful or not
     */
    fun addConnection(targetPos: BlockPos, targetFacing: Direction? = null): Boolean

    /**
     * Remove a connection
     *
     * @param pos Position of other end of connection
     */
    fun removeConnection(targetPos: BlockPos)

    /**
     * An ArrayList of connections
     */
    val connections: HashMap<BlockPos, WireConnection>
}