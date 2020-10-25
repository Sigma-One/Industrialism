package sigmaone.industrialism.block.wiring

import net.minecraft.client.render.*
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.state.property.Properties.FACING
import net.minecraft.util.math.Direction
import sigmaone.industrialism.util.CatenaryHelper
import util.WiringRenderLayer
import kotlin.math.*


class WireRenderer(dispatcher: BlockEntityRenderDispatcher?) : BlockEntityRenderer<BlockEntityWireNode>(dispatcher) {
    override fun render(entity: BlockEntityWireNode, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        for (conn in entity.connections) {
            matrices.push()
            if (entity.world!!.getBlockEntity(conn.key) != null && entity.world!!.getBlockEntity(conn.key) is BlockEntityWireNode) {

                /*val vertexAOffsets: FloatArray = when (entity.world!!.getBlockState(entity.pos).get(Properties.FACING)) {
                    Direction.UP    -> floatArrayOf(0.5f, 1f / 16f * 4f, 0.5f)
                    Direction.DOWN  -> floatArrayOf(0.5f, 1f / 16f * 12f, 0.5f)
                    Direction.NORTH -> floatArrayOf(0.5f, 0.5f, 1f / 16f * 12f)
                    Direction.SOUTH -> floatArrayOf(0.5f, 0.5f, 1f / 16f * 4f)
                    Direction.EAST  -> floatArrayOf(1f / 16f * 4f, 0.5f, 0.5f)
                    Direction.WEST  -> floatArrayOf(1f / 16f * 12f, 0.5f, 0.5f)
                    else -> throw IllegalStateException("Illegal Facing")
                }
                val vertexBOffsets: FloatArray = when (entity.world!!.getBlockState(conn).get(Properties.FACING)) {
                    Direction.UP    -> floatArrayOf(0.5f, 1f / 16f * 4f, 0.5f)
                    Direction.DOWN  -> floatArrayOf(0.5f, 1f / 16f * 12f, 0.5f)
                    Direction.NORTH -> floatArrayOf(0.5f, 0.5f, 1f / 16f * 12f)
                    Direction.SOUTH -> floatArrayOf(0.5f, 0.5f, 1f / 16f * 4f)
                    Direction.EAST  -> floatArrayOf(1f / 16f * 4f, 0.5f, 0.5f)
                    Direction.WEST  -> floatArrayOf(1f / 16f * 12f, 0.5f, 0.5f)
                    else -> throw IllegalStateException("Illegal Facing")
                }*/

                val vertices = vertexConsumers.getBuffer(WiringRenderLayer.getWiring())
                val vertexB = floatArrayOf(
                        conn.key.x - entity.pos.x.toFloat(),
                        conn.key.y - entity.pos.y.toFloat(),
                        conn.key.z - entity.pos.z.toFloat()
                )

                val vertexAOffsets: FloatArray = when (entity.world!!.getBlockState(entity.pos).get(FACING)) {
                    Direction.UP -> floatArrayOf(0.5f, 1f / 16f * 4f, 0.5f)
                    Direction.DOWN -> floatArrayOf(0.5f, 1f / 16f * 12f, 0.5f)
                    Direction.NORTH -> floatArrayOf(0.5f, 0.5f, 1f / 16f * 12f)
                    Direction.SOUTH -> floatArrayOf(0.5f, 0.5f, 1f / 16f * 4f)
                    Direction.EAST -> floatArrayOf(1f / 16f * 4f, 0.5f, 0.5f)
                    Direction.WEST -> floatArrayOf(1f / 16f * 12f, 0.5f, 0.5f)
                    else -> throw IllegalStateException("Illegal Facing")
                }
                val vertexBOffsets: FloatArray = when (entity.world!!.getBlockState(conn.key).get(FACING)) {
                    Direction.UP -> floatArrayOf(0.5f, 1f / 16f * 4f, 0.5f)
                    Direction.DOWN -> floatArrayOf(0.5f, 1f / 16f * 12f, 0.5f)
                    Direction.NORTH -> floatArrayOf(0.5f, 0.5f, 1f / 16f * 12f)
                    Direction.SOUTH -> floatArrayOf(0.5f, 0.5f, 1f / 16f * 4f)
                    Direction.EAST -> floatArrayOf(1f / 16f * 4f, 0.5f, 0.5f)
                    Direction.WEST -> floatArrayOf(1f / 16f * 12f, 0.5f, 0.5f)
                    else -> throw IllegalStateException("Illegal Facing")
                }

                val vertexA = floatArrayOf(0f, entity.pos.y.toFloat(), 0f)

                val hDiff = sqrt(
                        ((vertexB[0] - vertexA[0]).pow(2)) + ((vertexB[2] - vertexA[2]).pow(2))
                )

                val heights = CatenaryHelper.calculateHeights(conn.value.xShift, conn.value.yShift, hDiff, conn.value.coefficient, 10)

                val m: Float = if ((vertexB[2] - vertexA[2]) != 0f) {
                    ((vertexB[0] - vertexA[0]) / (vertexB[2] - vertexA[2]))
                }
                else {
                    0f
                }
                val ht = hDiff / heights.size
                var dz = ht / (sqrt(m.pow(2) + 1))
                var dx = m * dz

                val angle = atan2(vertexB[0] - vertexA[0], vertexB[2] - vertexA[2]) * (180 / PI)

                when {
                       angle >= 91.0
                    || angle <= -91.0     -> { dz = -dz; dx = -dx }
                    angle in 89.0..91.0   -> { dx = dz; dz = 0f }
                    angle in -91.0..-89.0 -> { dx = -dz; dz = 0f }
                }

                val colour = conn.value.wireColour

                for ((i, v) in heights.withIndex()) {
                    var vb = if (i == heights.lastIndex) {
                        vertexB[1]
                    }
                    else {
                        heights[i + 1] - entity.pos.y
                    }
                    val va = v - entity.pos.y
                    vertices.vertex(matrices.peek().model, vertexA[0] + i*dx, va - conn.value.wireThickness, vertexA[2] + i*dz)
                            .color(colour[0], colour[1], colour[2], 255)
                            .light(light)
                            .next()
                    vertices.vertex(matrices.peek().model, vertexA[0] + i*dx, va, vertexA[2] + i*dz)
                            .color(colour[0], colour[1], colour[2], 255)
                            .light(light)
                            .next()
                    vertices.vertex(matrices.peek().model, vertexA[0] + (i+1)*dx, vb, vertexA[2] + (i+1)*dz)
                            .color(colour[0], colour[1], colour[2], 255)
                            .light(light)
                            .next()
                    vertices.vertex(matrices.peek().model, vertexA[0] + (i+1)*dx, vb - conn.value.wireThickness, vertexA[2] + (i+1)*dz)
                            .color(colour[0], colour[1], colour[2], 255)
                            .light(light)
                            .next()
                }

                matrices.pop()
            }
        }
    }

    override fun rendersOutsideBoundingBox(blockEntity: BlockEntityWireNode): Boolean {
        return true
    }
}
