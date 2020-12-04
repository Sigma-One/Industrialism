package sigmaone.industrialism.component.wiring

import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtHelper
import net.minecraft.state.property.Properties
import net.minecraft.state.property.Properties.FACING
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import sigmaone.industrialism.block.IBlockEntityRefreshable
import sigmaone.industrialism.component.Component
import sigmaone.industrialism.energy.WireConnection
import sigmaone.industrialism.item.ItemWireSpool
import sigmaone.industrialism.util.CatenaryHelper
import java.util.*
import kotlin.collections.HashMap

open class ComponentWireNode(
    owner: BlockEntity,
    val height: Double,
    val maxConnections: Int,
    val wireTypes: Array<ItemWireSpool>,
    val isRelay: Boolean = false,
):
    Component(owner)
{
    val connections: HashMap<BlockPos, WireConnection> = hashMapOf()
    var facing: Direction? = null
    get() {
        return if (field == null) {
            owner.world!!.getBlockState(owner.pos).get(FACING)
        }
        else {
            field
        }
    }

    val networkEndpoints: ArrayList<BlockPos>
        get() {
            val visited = ArrayList<BlockPos>()
            val endpoints = ArrayList<BlockPos>()
            val stack = Stack<BlockPos>()
            // Add connected nodes to stack and self to visited nodes
            stack.push(this.owner.pos)
            visited.add(this.owner.pos)

            // Loop until stack is empty
            while (!stack.isEmpty()) {
                val current = owner.world!!.getBlockEntity(stack.pop())
                if (current is IComponentWireNode) {
                    // Loop through connections
                    for (pos in current.componentWireNode.connections.keys) {
                        val connection = owner.world!!.getBlockEntity(pos)
                        if (!visited.contains(pos) && connection is IComponentWireNode) {
                            // Add any found sinks to sink set
                            if (!connection.componentWireNode.isRelay) {
                                endpoints.add(pos)
                            }
                            // Add connected nodes to stack and self to visited nodes
                            stack.push(pos)
                            visited.add(pos)
                        }
                    }
                }
            }
            return endpoints
        }

    fun addConnection(target: BlockPos, targetFacing: Direction, targetHeight: Double, wireType: ItemWireSpool): Boolean {
        if (connections.size < maxConnections) {
            val vertexB = offsetPosition(target, targetFacing, targetHeight)
            val vertexA = offsetPosition(owner.pos, facing!!, height)

            val catenaryInfo = CatenaryHelper.solveCatenary(vertexA, vertexB, 1.0125f)
            connections[target] = WireConnection(catenaryInfo[2], catenaryInfo[0], catenaryInfo[1], wireType)
            if (owner is IBlockEntityRefreshable) {owner.refresh()}
            return true
        }
        return false
    }

    fun removeAllConnections() {
        while (connections.isNotEmpty()) {
            removeConnection(connections.keys.elementAt(0))
        }
    }

    fun removeConnection(targetPos: BlockPos) {
        val target = owner.world!!.getBlockEntity(targetPos)
        if (target is IComponentWireNode) {
            target.componentWireNode.connections.remove(owner.pos)
        }
        connections.remove(targetPos)
        if (owner is IBlockEntityRefreshable) {owner.refresh()}
    }

    private fun offsetPosition(pos: BlockPos, facing: Direction, height: Double): Vec3d {
        val offsets = when (facing) {
            Direction.DOWN  -> Vec3d(0.50, 1-height+0.15, 0.50)
            Direction.UP    -> Vec3d(0.50, height, 0.50)
            Direction.NORTH -> Vec3d(0.50, 0.50, 1-height)
            Direction.SOUTH -> Vec3d(0.50, 0.50, height)
            Direction.EAST  -> Vec3d(height, 0.50, 0.50)
            Direction.WEST  -> Vec3d(1-height, 0.50, 0.50)
            else -> throw IllegalStateException("Illegal orientation")
        }

        return Vec3d(
            pos.x + offsets.x,
            pos.y + offsets.y,
            pos.z + offsets.z
        )
    }

    private fun testConnectionCollisions(targetPos: BlockPos): Boolean {
        val target = owner.world!!.getBlockEntity(targetPos)
        if (target !is IComponentWireNode) {
            return false
        }
        val conn = connections[targetPos]!!

        val vertexB = offsetPosition(targetPos, target.componentWireNode.facing!!, target.componentWireNode.height)
        val vertexA = offsetPosition(owner.pos, facing!!, height)

        val result = CatenaryHelper.raytraceCatenary(owner.world!!, vertexA, vertexB, conn.xShift, conn.yShift, conn.coefficient, 10)

        if (result == null) {
            return false
        }
        return true
    }

    open fun tick() {
        // Deal with collisions
        if (connections.size > 0) {
            val removalBuffer = ArrayList<BlockPos>()
            for (connection in connections.keys) {
                if (testConnectionCollisions(connection)) {
                    removalBuffer.add(connection)
                }
            }
            for (connection in removalBuffer) {
                removeConnection(connection)
            }
        }
    }

    fun fromClientTag(tag: CompoundTag?) {
        val connectionAmount = tag!!.getInt("connection_amount")
        facing = Direction.byName(tag.getString("facing"))
        if (connectionAmount > 0) {
            for (i in 0 until connectionAmount) {
                val connTag = tag.getCompound(i.toString())
                val wireType = Registry.ITEM.get(Identifier.tryParse(connTag.getString("wiretype"))) as ItemWireSpool
                val pos = NbtHelper.toBlockPos(connTag.getCompound("position"))
                var targetOrientation = Direction.byName(connTag.getString("facing"))
                if (targetOrientation == null) {
                   targetOrientation = owner.world!!.getBlockState(pos).get(Properties.FACING)
                }
                val targetHeight = connTag.getDouble("height")
                if (!connections.keys.contains(pos)) {
                    addConnection(pos, targetOrientation!!, targetHeight, wireType)
                }
            }
        }
    }

    fun toClientTag(tag: CompoundTag?): CompoundTag {
        tag!!.putInt("connection_amount", connections.size)
        if (connections.size > 0) {
            for ((i, connection) in connections.keys.withIndex()) {
                val connTag = CompoundTag()
                val target = owner.world!!.getBlockEntity(connection)
                if (target is IComponentWireNode) {
                    connTag.putString("wiretype", Registry.ITEM.getId(connections[connection]!!.wireType).toString())
                    connTag.put("position", NbtHelper.fromBlockPos(connection))
                    connTag.putString("orientation", target.componentWireNode.facing.toString ())
                    connTag.putDouble("height", target.componentWireNode.height)
                    tag.put(i.toString(), connTag)
                }
            }
        }
        tag.putString("facing", facing.toString())
        return tag
    }

    fun fromTag(tag: CompoundTag?) {
        val connectionAmount = tag!!.getInt("connection_amount")
        facing = Direction.byName(tag.getString("facing"))
        if (connectionAmount > 0) {
            for (i in 0 until connectionAmount) {
                val connTag = tag.getCompound(i.toString())
                val wireType = Registry.ITEM.get(Identifier.tryParse(connTag.getString("wiretype"))) as ItemWireSpool
                val pos = NbtHelper.toBlockPos(connTag.getCompound("position"))
                var targetOrientation = Direction.byName(connTag.getString("facing"))
                if (targetOrientation == null) {
                    targetOrientation = owner.world!!.getBlockState(pos).get(Properties.FACING)
                }
                val targetHeight = connTag.getDouble("height")
                if (!connections.keys.contains(pos)) {
                    addConnection(pos, targetOrientation!!, targetHeight, wireType)
                }
            }
        }
    }

    fun toTag(tag: CompoundTag?): CompoundTag {
        tag!!.putInt("connection_amount", connections.size)
        if (connections.size > 0) {
            for ((i, connection) in connections.keys.withIndex()) {
                val connTag = CompoundTag()
                val target = owner.world!!.getBlockEntity(connection)
                if (target is IComponentWireNode) {
                    connTag.putString("wiretype", Registry.ITEM.getId(connections[connection]!!.wireType).toString())
                    connTag.put("position", NbtHelper.fromBlockPos(connection))
                    connTag.putString("orientation", target.componentWireNode.facing.toString ())
                    connTag.putDouble("height", target.componentWireNode.height)
                    tag.put(i.toString(), connTag)
                }
            }
        }
        tag.putString("facing", facing.toString())
        return tag
    }
}