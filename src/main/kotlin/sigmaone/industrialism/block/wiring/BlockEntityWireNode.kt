package sigmaone.industrialism.block.wiring

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.Industrialism.InputConfig
import sigmaone.industrialism.block.BlockEntityConnectableEnergyContainer
import sigmaone.industrialism.block.IConfigurable
import team.reborn.energy.Energy
import team.reborn.energy.EnergyTier
import java.util.*
import kotlin.collections.HashSet
import kotlin.math.min


class BlockEntityWireNode :
        BlockEntityConnectableEnergyContainer(Industrialism.CONNECTOR_T0, 32.toDouble(), EnergyTier.LOW),
        IWireNode,
        BlockEntityClientSerializable,
        IConfigurable {
    private val maxConnections = 16
    override var connections: HashSet<BlockPos> = HashSet()
    var IOstate: InputConfig = InputConfig.NONE

    override var sideConfig: HashMap<Direction, InputConfig> = hashMapOf(
            Direction.NORTH to InputConfig.NONE,
            Direction.SOUTH to InputConfig.NONE,
            Direction.EAST  to InputConfig.NONE,
            Direction.WEST  to InputConfig.NONE,
            Direction.UP    to InputConfig.NONE,
            Direction.DOWN  to InputConfig.NONE
    )

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
    }

    private val connectedSinks: HashSet<BlockEntityWireNode>
        get() {
            val visited = HashSet<BlockPos?>()
            val sinks = HashSet<BlockEntityWireNode>()
            val nodeStack = Stack<BlockEntityWireNode>()
            // Add connected nodes to stack and self to visited nodes
            nodeStack.push(this)
            visited.add(getPos())

            // Loop until stack is empty
            while (!nodeStack.isEmpty()) {
                val currentNode = nodeStack.pop()
                // Loop through connections
                for (connectedPos in currentNode.connections) {
                    if (!visited.contains(connectedPos)) {
                        // Add any found sinks to sink set
                        val blockEntity = getWorld()!!.getBlockEntity(connectedPos) as BlockEntityWireNode?
                        if (blockEntity != null) {
                            if (blockEntity.IOstate == InputConfig.OUTPUT) {
                                sinks.add(blockEntity)
                            }
                            // Add connected nodes to stack and self to visited nodes
                            nodeStack.push(blockEntity)
                            visited.add(connectedPos)
                        }
                    }
                }
            }
            return sinks
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
        if (IOstate == InputConfig.INPUT && connectedSinks.isNotEmpty()) {
            val amount = tier.maxOutput / connectedSinks.size
            for (node in connectedSinks) {
                val target = Energy.of(node)
                val source = Energy.of(this)
                val maxMove = min(target.maxInput, min(source.maxOutput, amount.toDouble()))
                Energy.of(node).insert(source.extract(maxMove))
            }
        }
        refresh()
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
        super.toTag(tag)
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