package sigmaone.industrialism.block.wiring

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtHelper
import net.minecraft.tag.TagManager
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.Industrialism.InputConfig
import sigmaone.industrialism.block.BlockEntityConnectableEnergyContainer
import sigmaone.industrialism.block.IConfigurable
import sigmaone.industrialism.energy.WireConnection
import sigmaone.industrialism.util.CatenaryHelper
import team.reborn.energy.Energy
import team.reborn.energy.EnergyTier
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.min


class BlockEntityWireNode :
        BlockEntityConnectableEnergyContainer(Industrialism.CONNECTOR_T0, 32.toDouble(), EnergyTier.LOW),
        IWireNode,
        BlockEntityClientSerializable,
        IConfigurable {
    private val maxConnections = 16
    override var connections: HashMap<BlockPos, WireConnection> = HashMap()
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
                for (connection in currentNode.connections.keys) {
                    if (!visited.contains(connection)) {
                        // Add any found sinks to sink set
                        val blockEntity = getWorld()!!.getBlockEntity(connection) as BlockEntityWireNode?
                        if (blockEntity != null) {
                            if (blockEntity.IOstate == InputConfig.OUTPUT) {
                                sinks.add(blockEntity)
                            }
                            // Add connected nodes to stack and self to visited nodes
                            nodeStack.push(blockEntity)
                            visited.add(connection)
                        }
                    }
                }
            }
            return sinks
        }

    override fun addConnection(pos: BlockPos): Boolean {
        if (connections.size < maxConnections) {
            val catenaryInfo = CatenaryHelper.solveCatenary(this.pos, pos, 1.05f)
            connections.put(pos, WireConnection(catenaryInfo[2], catenaryInfo[0], catenaryInfo[1]))
            refresh()
            return true
        }
        return false
    }

    fun removeAllConnections() {
        while (connections.isNotEmpty()) {
            removeConnection(connections.keys.elementAt(0))
        }
    }

    override fun removeConnection(pos: BlockPos) {
        val blockEntity = getWorld()!!.getBlockEntity(pos)
        if (blockEntity != null) {
            (blockEntity as BlockEntityWireNode).connections.remove(getPos())
        }
        connections.remove(pos)
        refresh()
    }

    override fun tick() {
        super.tick()
        // Remove nonexistent connections, just in case
        if (connections.size > 0) {
            val removalBuffer = HashSet<BlockPos?>()
            for (pos in connections.keys) {
                if (getWorld() != null && getWorld()!!.getBlockEntity(pos) == null) {
                    removalBuffer.add(pos)
                }
            }
            for (pos in removalBuffer) {
                removeConnection(pos!!)
            }
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
                val pos = NbtHelper.toBlockPos(tag.getCompound(i.toString()))
                if (!connections.keys.contains(pos)) {
                    addConnection(pos)
                }
            }
        }
    }

    override fun toClientTag(tag: CompoundTag): CompoundTag {
        tag.putInt("connection_amount", connections.size)
        if (connections.size > 0) {
            for ((i, pos) in connections.keys.withIndex()) {
                tag.put(i.toString(), NbtHelper.fromBlockPos(pos))
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
                val pos = NbtHelper.toBlockPos(tag.getCompound(i.toString()))
                if (!connections.keys.contains(pos)) {
                    addConnection(pos)
                }
            }
        }
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)
        tag.putInt("connection_amount", connections.size)
        tag.putInt("io_mode", IOstate.ordinal)
        if (connections.size > 0) {
            for ((i, pos) in connections.keys.withIndex()) {
                tag.put(i.toString(), NbtHelper.fromBlockPos(pos))
            }
        }
        return tag
    }
}