package sigmaone.industrialism.block.wiring

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.Industrialism.InputConfig
import sigmaone.industrialism.block.BlockEntitySidedEnergyContainer
import sigmaone.industrialism.block.IConfigurable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class BlockEntityWireNode : BlockEntitySidedEnergyContainer(Industrialism.CONNECTOR_T0, 10f, sideConfig), IWireNode, BlockEntityClientSerializable, IConfigurable {
    val maxConnections = 16
    override var connections: HashSet<BlockPos> = HashSet()
    var IOstate: InputConfig = InputConfig.NONE

    companion object {
        private val sideConfig = HashMap<Direction, InputConfig>()

        init {
            sideConfig[Direction.NORTH] = InputConfig.NONE
            sideConfig[Direction.SOUTH] = InputConfig.NONE
            sideConfig[Direction.EAST ] = InputConfig.NONE
            sideConfig[Direction.WEST ] = InputConfig.NONE
            sideConfig[Direction.UP   ] = InputConfig.NONE
            sideConfig[Direction.DOWN ] = InputConfig.NONE
        }
    }

    override fun refresh() {
        super.refresh()
        if (getWorld() != null && !getWorld()!!.isClient) {
            sync()
        }
    }

    fun cycleIOState() {
        IOstate = when (IOstate) {
            InputConfig.NONE -> InputConfig.INPUT
            InputConfig.INPUT -> InputConfig.OUTPUT
            InputConfig.OUTPUT -> InputConfig.NONE
        }
    }// Add connected nodes to stack and self to visited nodes// Add any found sources to source set// Loop through connections

    // Loop until stack is empty
    private val connectedSources: HashSet<BlockEntityWireNode>
        get() {
            val visited = HashSet<BlockPos?>()
            val sources = HashSet<BlockEntityWireNode>()
            val nodeStack = Stack<BlockEntityWireNode>()
            nodeStack.push(this)
            visited.add(getPos())

            // Loop until stack is empty
            while (!nodeStack.isEmpty()) {
                val currentNode = nodeStack.pop()
                // Loop through connections
                for (connectedPos in currentNode.connections) {
                    if (!visited.contains(connectedPos)) {
                        // Add any found sources to source set
                        val blockEntity = getWorld()!!.getBlockEntity(connectedPos) as BlockEntityWireNode?
                        if (blockEntity != null) {
                            if (blockEntity.IOstate == InputConfig.OUTPUT) {
                                sources.add(blockEntity)
                            }
                            // Add connected nodes to stack and self to visited nodes
                            nodeStack.push(blockEntity)
                            visited.add(connectedPos)
                        }
                    }
                }
            }
            return sources
        }

    override fun addConnection(pos: BlockPos): Boolean {
        if (connections.size < maxConnections) {
            connections.add(pos)
            refresh()
            return true
        }
        return false
    }

    fun removeAllConnections() {
        while (connections.isNotEmpty()) {
            removeConnection(connections.elementAt(0))
        }
    }

    override fun removeConnection(pos: BlockPos) {
        val blockEntity = getWorld()!!.getBlockEntity(pos) as BlockEntityWireNode
        blockEntity.connections.remove(getPos())
        connections.remove(pos)
        refresh()
    }

    override fun tick() {
        super.tick()
        // Remove nonexistent connections, just in case
        if (connections.size > 0) {
            val removalBuffer = HashSet<BlockPos?>()
            for (pos in connections) {
                if (getWorld() != null && getWorld()!!.getBlockEntity(pos) == null) {
                    removalBuffer.add(pos)
                }
            }
            connections.removeAll(removalBuffer)
        }
        if (IOstate == InputConfig.INPUT) {
            val acceptors = ArrayList<BlockPos>()
            for (node in connectedSources) {
                acceptors.add(node.getPos())
            }
            val packet = maxTransfer / acceptors.size
            for (node in acceptors) {
                sendEnergy(node, packet)
            }
        }
    }

    override fun fromClientTag(tag: CompoundTag) {
        val connectionAmount = tag.getInt("connection_amount")
        if (connectionAmount > 0) {
            for (i in 0 until connectionAmount) {
                val pos = tag.getIntArray(i.toString())
                addConnection(BlockPos(pos[0], pos[1], pos[2]))
            }
        }
    }

    override fun toClientTag(tag: CompoundTag): CompoundTag {
        tag.putInt("connection_amount", connections.size)
        if (connections.size > 0) {
            for (i in connections.indices) {
                tag.putIntArray(i.toString(), intArrayOf(
                        connections.elementAt(i).x,
                        connections.elementAt(i).y,
                        connections.elementAt(i).z)
                )
            }
        }
        return tag
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)
        val connectionAmount = tag.getInt("connection_amount")
        IOstate = InputConfig.values()[tag.getInt("io_mode")]
        if (connectionAmount > 0) {
            for (i in 0 until connectionAmount) {
                val pos = tag.getIntArray(i.toString())
                if (!connections.contains(BlockPos(pos[0], pos[1], pos[2]))) {
                    addConnection(BlockPos(pos[0], pos[1], pos[2]))
                }
            }
        }
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        var tag = tag
        tag = super.toTag(tag)
        tag.putInt("connection_amount", connections.size)
        tag.putInt("io_mode", IOstate.ordinal)
        if (connections.size > 0) {
            for (i in connections.indices) {
                tag.putIntArray(i.toString(), intArrayOf(
                        connections.elementAt(i).x,
                        connections.elementAt(i).y,
                        connections.elementAt(i).z)
                )
            }
        }
        return tag
    }
}