package sigmaone.industrialism.block.wiring

import net.minecraft.client.render.*
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import sigmaone.industrialism.util.CatenaryHelper
import util.WiringRenderLayer
import kotlin.math.*


class WireRenderer(dispatcher: BlockEntityRenderDispatcher?) : BlockEntityRenderer<BlockEntityWireNode>(dispatcher) {
    override fun render(entity: BlockEntityWireNode, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        for (conn in entity.connections) {
            matrices.push()
            if (entity.world!!.getBlockEntity(conn.key) != null && entity.world!!.getBlockEntity(conn.key) is BlockEntityWireNode) {
                val vertices = vertexConsumers.getBuffer(WiringRenderLayer.getWiring())
                val vertexB = floatArrayOf(
                        conn.key.x - entity.pos.x.toFloat(),
                        conn.key.y - entity.pos.y.toFloat(),
                        conn.key.z - entity.pos.z.toFloat()
                )
                val vertexA = floatArrayOf(
                        0f,
                        entity.pos.y.toFloat(),
                        0f
                )

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
                    val bya = if (i == heights.lastIndex) {
                        vertexB[1]
                    }
                    else {
                        heights[i + 1] - vertexA[1]
                    }
                    val ayb = v - entity.pos.y
                    val aya = ayb - conn.value.wireThickness
                    val byb = bya - conn.value.wireThickness
                    val ax = vertexA[0] + i*dx
                    val az = vertexA[2] + i*dz
                    val bx = vertexA[0] + (i+1)*dx
                    val bz = vertexA[2] + (i+1)*dz
                    vertices.vertex(matrices.peek().model, ax+0.5f, aya+0.5f, az+0.5f)
                            .color(colour[0], colour[1], colour[2], 255)
                            .light(light)
                            .next()
                    vertices.vertex(matrices.peek().model, ax+0.5f, ayb+0.5f, az+0.5f)
                            .color(colour[0], colour[1], colour[2], 255)
                            .light(light)
                            .next()
                    vertices.vertex(matrices.peek().model, bx+0.5f, bya+0.5f, bz+0.5f)
                            .color(colour[0], colour[1], colour[2], 255)
                            .light(light)
                            .next()
                    vertices.vertex(matrices.peek().model, bx+0.5f, byb+0.5f, bz+0.5f)
                            .color(colour[0], colour[1], colour[2], 255)
                            .light(light)
                            .next()
                }

                for ((i, v) in heights.withIndex()) {
                    val bya = if (i == heights.lastIndex) {
                        vertexB[1]
                    }
                    else {
                        heights[i + 1] - vertexA[1]
                    }
                    val ayb = v - entity.pos.y
                    val aya = ayb - conn.value.wireThickness / 2
                    val byb = bya - conn.value.wireThickness / 2
                    val ax = vertexA[0] + i * dx
                    val az = vertexA[2] + i * dz
                    val bx = vertexA[0] + (i+1)*dx
                    val bz = vertexA[2] + (i+1)*dz

                    val vn = Vector3f(0f, ayb - aya, 0f)
                    val vd = Vector3f(bx - ax, 0f, bz - az)
                    vn.cross(vd)
                    vn.normalize()

                    val aa = Vector3f(
                            ax + (conn.value.wireThickness/2) * vn.x,
                            aya,
                            az + (conn.value.wireThickness/2) * vn.z
                    )
                    val ab = Vector3f(
                            ax - (conn.value.wireThickness/2) * vn.x,
                            aya,
                            az - (conn.value.wireThickness/2) * vn.z
                    )
                    val ba = Vector3f(
                            bx + (conn.value.wireThickness/2) * vn.x,
                            byb,
                            bz + (conn.value.wireThickness/2) * vn.z
                    )
                    val bb = Vector3f(
                            bx - (conn.value.wireThickness/2) * vn.x,
                            byb,
                            bz - (conn.value.wireThickness/2) * vn.z
                    )

                    vertices.vertex(matrices.peek().model, aa.x+0.5f, aa.y+0.5f, aa.z+0.5f)
                            .color(colour[0], colour[1], colour[2], 255)
                            .light(light)
                            .next()
                    vertices.vertex(matrices.peek().model, ab.x+0.5f, ab.y+0.5f, ab.z+0.5f)
                            .color(colour[0], colour[1], colour[2], 255)
                            .light(light)
                            .next()
                    vertices.vertex(matrices.peek().model, ba.x+0.5f, ba.y+0.5f, ba.z+0.5f)
                            .color(colour[0], colour[1], colour[2], 255)
                            .light(light)
                            .next()
                    vertices.vertex(matrices.peek().model, bb.x+0.5f, bb.y+0.5f, bb.z+0.5f)
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
