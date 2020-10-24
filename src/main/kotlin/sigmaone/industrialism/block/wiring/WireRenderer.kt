package sigmaone.industrialism.block.wiring

import com.google.common.collect.ImmutableList
import net.minecraft.client.render.*
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.state.property.Properties.FACING
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import org.lwjgl.opengl.GL11
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

                /*val vertices = vertexConsumers.getBuffer(RenderLayer.getLeash())

                val hDiff = sqrt(
                        ((conn.key.x.toFloat() - entity.pos.x.toFloat()).pow(2)) + ((conn.key.z.toFloat() - entity.pos.z.toFloat()).pow(2))
                )

                val heights = CatenaryHelper.calculateHeights(conn.value.xShift, conn.value.yShift, hDiff, conn.value.coefficient, 10)

                val m: Float = if ((conn.key.z - entity.pos.z) != 0) {
                    ((conn.key.x - entity.pos.x) / (conn.key.z - entity.pos.z)).toFloat()
                }.<VertexFormatElement>
                else {
                    0f
                }
                val ht = hDiff / heights.size
                val dz = ht / (sqrt(m.pow(2) + 1))
                val dx = m * dz

                val vertexPositions: ArrayList<FloatArray> = ArrayList()

                for ((i, y) in heights.withIndex()) {
                    vertexPositions.add(floatArrayOf(i * dx, y, i * dz))
                }

                //for ((i, vertex) in vertexPositions.withIndex()) {
                /*vertices.quad(matrices.peek(), BakedQuad(intArrayOf(
                        0, 0, 0,
                ), -1, Direction.UP, null, true), 1f, 0.5f, 0f, light, overlay)
                vertices.quad(matrices.peek(), BakedQuad(intArrayOf(
                        16, 0, 0,
                ), -1, Direction.UP, null, true), 1f, 0.5f, 0f, light, overlay)
                vertices.quad(matrices.peek(), BakedQuad(intArrayOf(
                        16, 0, 16,
                ), -1, Direction.UP, null, true), 1f, 0.5f, 0f, light, overlay)
                vertices.quad(matrices.peek(), BakedQuad(intArrayOf(
                        0, 0, 16,
                ), -1, Direction.UP, null, true), 1f, 0.5f, 0f, light, overlay)
                vertices.next()*/
                //}
                vertices.vertex(matrices.peek().model, 0.1f, 0.1f, 0.1f).color(100, 50, 10, 255).next()
                vertices.vertex(matrices.peek().model, 0.5f, 0.5f, 0.5f).color(100, 50, 10, 255).next()*/

                val vertices = vertexConsumers.getBuffer(WiringRenderLayer.getWiring())
                val vertexBBase = floatArrayOf(
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

                val vertexA = floatArrayOf(0f, 0f, 0f)
                val vertexB = vertexBBase
                //val vertexA = floatArrayOf(vertexAOffsets[0], vertexAOffsets[1], vertexAOffsets[2])
                //val vertexB = floatArrayOf(vertexBBase[0] + vertexBOffsets[0], vertexBBase[1] + vertexBOffsets[1], vertexBBase[2] + vertexBOffsets[2])

                val hDiff = sqrt(
                        ((vertexB[0] - vertexA[0]).pow(2)) + ((vertexB[2] - vertexA[2]).pow(2))
                )

                val heights = CatenaryHelper.calculateHeights(conn.value.xShift, conn.value.yShift, hDiff, conn.value.coefficient, 10)

                val m: Float = if ((vertexB[2] - vertexA[2]) != 0f) {
                    ((vertexB[0] - vertexA[0]) / (vertexB[2] - vertexA[2])).toFloat()
                }
                else {
                    0f
                }
                val ht = hDiff / heights.size
                var dz = ht / (sqrt(m.pow(2) + 1))
                var dx = m * dz

                val angle = atan2(vertexB[0] - vertexA[0], vertexB[2] - vertexA[2]) * (180/PI)
                val fdd: FloatArray = FloatArray(2)

                /*fdd = when(angle) {
                    in -180f..-90f -> floatArrayOf(-dx, -dz)
                    in -90f..0f -> floatArrayOf(dx, dz)
                    in 0f..90f -> floatArrayOf(dx, dz)
                    else -> floatArrayOf(-dx, -dz)
                }*/

                when {
                       angle >= 91.0
                    || angle <= -91.0     -> { dz = -dz; dx = -dx }
                    angle in 89.0..91.0   -> { dx = dz; dz = 0f }
                    angle in -91.0..-89.0 -> { dx = -dz; dz = 0f }
                }


                vertices.vertex(matrices.peek().model, vertexA[0], vertexA[1], vertexA[2]).color(0, 0, 0, 150).next()
                vertices.vertex(matrices.peek().model, vertexA[0], vertexA[1], vertexA[2]).color(255, 100, 10, 255).next()

                for ((i, v) in heights.withIndex()) {
                    if (i == 0) { continue }
                    vertices.vertex(matrices.peek().model, vertexA[0] + i*dx, v - (conn.key.y + entity.pos.y)/2, vertexA[2] + i*dz).color(255, 100, 10, 255).next()
                }
                vertices.vertex(matrices.peek().model, vertexB[0], vertexB[1], vertexB[2]).color(255, 100, 10, 255).next()
                vertices.vertex(matrices.peek().model, vertexB[0], vertexB[1], vertexB[2]).color(0, 0, 0, 150).next()


                //vertices.vertex(matrices.peek().model, vertexA[0], vertexA[1], vertexA[2]).color(100, 50, 10, 255)
                //vertices.vertex(matrices.peek().model, vertexB[0], vertexB[1], vertexB[2]).color(100, 50, 10, 255).next()
                matrices.pop()
            }
        }
    }

    override fun rendersOutsideBoundingBox(blockEntity: BlockEntityWireNode): Boolean {
        return true
    }
}
