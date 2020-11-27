package sigmaone.industrialism.block.wiring

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtHelper
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.Industrialism.InputConfig
import sigmaone.industrialism.block.BlockEntityConnectableEnergyContainer
import sigmaone.industrialism.block.IConfigurable
import sigmaone.industrialism.energy.WireConnection
import sigmaone.industrialism.util.CatenaryHelper
import team.reborn.energy.Energy
import team.reborn.energy.EnergyTier
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.*


class BlockEntityWireNode :
        BlockEntityConnectableEnergyContainer(Industrialism.CONNECTOR_T0, 32.toDouble(), EnergyTier.LOW),
        IWireNode,
        BlockEntityClientSerializable,
        IConfigurable {
    private val maxConnections = 16
    var orientation: Direction? = null
    override var connections: HashMap<BlockPos, WireConnection> = HashMap()
    var IOstate: InputConfig = InputConfig.NONE
    val height = 0.23

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

    override fun addConnection(targetPos: BlockPos, targetFacing: Direction?): Boolean {
        if (connections.size < maxConnections) {
            var targetFacing = targetFacing
            if (targetFacing == null) {
                targetFacing = world!!.getBlockState(targetPos).get(Properties.FACING)
            }
            val facing = if (orientation == null) {
                world!!.getBlockState(pos).get(Properties.FACING)
            }
            else {
                orientation
            }

            val targetOffsets = when (targetFacing) {
                Direction.DOWN  -> Vec3d(0.50, 1-height+0.15, 0.50)
                Direction.UP    -> Vec3d(0.50, height, 0.50)
                Direction.NORTH -> Vec3d(0.50, 0.50, 1-height)
                Direction.SOUTH -> Vec3d(0.50, 0.50, height)
                Direction.EAST  -> Vec3d(height, 0.50, 0.50)
                Direction.WEST  -> Vec3d(1-height, 0.50, 0.50)
                else -> throw IllegalStateException("Illegal orientation")
            }

            val offsets = when (facing) {
                Direction.DOWN  -> Vec3d(0.50, 1-height+0.15, 0.50)
                Direction.UP    -> Vec3d(0.50, height, 0.50)
                Direction.NORTH -> Vec3d(0.50, 0.50, 1-height)
                Direction.SOUTH -> Vec3d(0.50, 0.50, height)
                Direction.EAST  -> Vec3d(height, 0.50, 0.50)
                Direction.WEST  -> Vec3d(1-height, 0.50, 0.50)
                else -> throw IllegalStateException("Illegal orientation")
            }
            val vertexA = Vec3d(
                    pos.x + offsets.x,
                    pos.y + offsets.y,
                    pos.z + offsets.z
            )
            val vertexB = Vec3d(
                    targetPos.x + targetOffsets.x,
                    targetPos.y + targetOffsets.y,
                    targetPos.z + targetOffsets.z
            )
            val catenaryInfo = CatenaryHelper.solveCatenary(vertexA, vertexB, 1.0125f)
            connections.put(targetPos, WireConnection(catenaryInfo[2], catenaryInfo[0], catenaryInfo[1], 0.05f, intArrayOf(220, 145, 85)))
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

    override fun removeConnection(targetPos: BlockPos) {
        val blockEntity = getWorld()!!.getBlockEntity(targetPos)
        if (blockEntity != null) {
            (blockEntity as BlockEntityWireNode).connections.remove(getPos())
        }
        connections.remove(targetPos)
        refresh()
    }

    private fun testConnectionCollisions(targetPos: BlockPos): Boolean {
        val conn = connections[targetPos]
        if (conn == null) {
            return false
        }

        val targetFacing = if ((world!!.getBlockEntity(targetPos) as BlockEntityWireNode).orientation == null) {
            world!!.getBlockState(targetPos).get(Properties.FACING)
        }
        else {
            (world!!.getBlockEntity(targetPos) as BlockEntityWireNode).orientation
        }
        val facing = if (orientation == null) {
            world!!.getBlockState(pos).get(Properties.FACING)
        }
        else {
            orientation
        }

        val targetOffsets = when (targetFacing) {
            Direction.DOWN  -> Vec3d(0.50, 1-height+0.15, 0.50)
            Direction.UP    -> Vec3d(0.50, height, 0.50)
            Direction.NORTH -> Vec3d(0.50, 0.50, 1-height)
            Direction.SOUTH -> Vec3d(0.50, 0.50, height)
            Direction.EAST  -> Vec3d(height, 0.50, 0.50)
            Direction.WEST  -> Vec3d(1-height, 0.50, 0.50)
            else -> throw IllegalStateException("Illegal orientation")
        }

        val offsets = when (facing) {
            Direction.DOWN  -> Vec3d(0.50, 1-height+0.15, 0.50)
            Direction.UP    -> Vec3d(0.50, height, 0.50)
            Direction.NORTH -> Vec3d(0.50, 0.50, 1-height)
            Direction.SOUTH -> Vec3d(0.50, 0.50, height)
            Direction.EAST  -> Vec3d(height, 0.50, 0.50)
            Direction.WEST  -> Vec3d(1-height, 0.50, 0.50)
            else -> throw IllegalStateException("Illegal orientation")
        }
        val vertexA = Vec3d(
                pos.x + offsets.x,
                pos.y + offsets.y,
                pos.z + offsets.z
        )
        val vertexB = Vec3d(
                targetPos.x + targetOffsets.x,
                targetPos.y + targetOffsets.y,
                targetPos.z + targetOffsets.z
        )
        val result = CatenaryHelper.raytraceCatenary(world!!, vertexA, vertexB, conn.xShift, conn.yShift, conn.coefficient, 10)

        if (result == null) {
            return false
        }
        return true
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
                else {
                    if (testConnectionCollisions(pos)) {
                        removalBuffer.add(pos)
                    }
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
        orientation = Direction.byName(tag.getString("orientation"))!!
        if (connectionAmount > 0) {
            for (i in 0 until connectionAmount) {
                val connTag = tag.getCompound(i.toString())
                val pos = NbtHelper.toBlockPos(connTag.getCompound(i.toString()))
                val targetOrientation = Direction.byName(connTag.getString("orientation"))
                if (!connections.keys.contains(pos)) {
                    addConnection(pos, targetOrientation)
                }
            }
        }
    }

    override fun toClientTag(tag: CompoundTag): CompoundTag {
        tag.putInt("connection_amount", connections.size)
        if (connections.size > 0) {
            for ((i, pos) in connections.keys.withIndex()) {
                val connTag = CompoundTag()
                connTag.put(i.toString(), NbtHelper.fromBlockPos(pos))
                connTag.putString("orientation", world!!.getBlockState(pos).get(Properties.FACING).toString())
                tag.put(i.toString(), connTag)
            }
        }
        tag.putString("orientation", world!!.getBlockState(pos).get(Properties.FACING).toString())
        return tag
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)
        val connectionAmount = tag.getInt("connection_amount")
        orientation = Direction.byName(tag.getString("orientation"))!!
        IOstate = InputConfig.values()[tag.getInt("io_mode")]
        if (connectionAmount > 0) {
            for (i in 0 until connectionAmount) {
                val connTag = tag.getCompound(i.toString())
                val pos = NbtHelper.toBlockPos(connTag.getCompound(i.toString()))
                val targetOrientation = Direction.byName(connTag.getString("orientation"))
                if (!connections.keys.contains(pos)) {
                    addConnection(pos, targetOrientation)
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
                val connTag = CompoundTag()
                connTag.put(i.toString(), NbtHelper.fromBlockPos(pos))
                connTag.putString("orientation", world!!.getBlockState(pos).get(Properties.FACING).toString())
                tag.put(i.toString(), connTag)
            }
        }
        tag.putString("orientation", world!!.getBlockState(pos).get(Properties.FACING).toString())
        return tag
    }
}