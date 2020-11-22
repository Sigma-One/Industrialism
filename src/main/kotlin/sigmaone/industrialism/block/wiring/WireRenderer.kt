package sigmaone.industrialism.block.wiring

import net.minecraft.client.render.*
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import sigmaone.industrialism.util.CatenaryHelper
import util.WiringRenderLayer
import kotlin.math.*


class WireRenderer(dispatcher: BlockEntityRenderDispatcher?) : BlockEntityRenderer<BlockEntityWireNode>(dispatcher) {
    override fun render(entity: BlockEntityWireNode, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        for (conn in entity.connections) {
            matrices.push()
            if (entity.world!!.getBlockEntity(conn.key) != null && entity.world!!.getBlockEntity(conn.key) is BlockEntityWireNode) {
                val vertices = vertexConsumers.getBuffer(WiringRenderLayer.getWiring())

                val facing = entity.world!!.getBlockState(entity.pos).get(Properties.FACING)
                val targetFacing = entity.world!!.getBlockState(conn.key).get(Properties.FACING)

                val targetOffsets = when (targetFacing) {
                    Direction.DOWN  -> Vec3d(0.50, 0.75, 0.50)
                    Direction.UP    -> Vec3d(0.50, 0.25, 0.50)
                    Direction.NORTH -> Vec3d(0.50, 0.50, 0.75)
                    Direction.SOUTH -> Vec3d(0.50, 0.50, 0.25)
                    Direction.EAST  -> Vec3d(0.25, 0.50, 0.50)
                    Direction.WEST  -> Vec3d(0.75, 0.50, 0.50)
                    else            -> throw IllegalStateException("Illegal orientation")
                }

                val offsets = when (facing) {
                    Direction.DOWN  -> Vec3d(0.50, 0.75, 0.50)
                    Direction.UP    -> Vec3d(0.50, 0.25, 0.50)
                    Direction.NORTH -> Vec3d(0.50, 0.50, 0.75)
                    Direction.SOUTH -> Vec3d(0.50, 0.50, 0.25)
                    Direction.EAST  -> Vec3d(0.25, 0.50, 0.50)
                    Direction.WEST  -> Vec3d(0.75, 0.50, 0.50)
                    else            -> throw IllegalStateException("Illegal orientation")
                }

                val vertexB = Vector3f(
                        (conn.key.x - entity.pos.x + targetOffsets.x).toFloat(),
                        (conn.key.y - entity.pos.y + targetOffsets.y).toFloat(),
                        (conn.key.z - entity.pos.z + targetOffsets.z).toFloat()
                )
                val vertexA = Vector3f(
                        offsets.x.toFloat(),
                        entity.pos.y.toFloat() + offsets.y.toFloat(),
                        offsets.z.toFloat()
                )

                val hDiff = sqrt(
                        ((vertexB.x - vertexA.x).pow(2)) + ((vertexB.z - vertexA.z).pow(2))
                )

                val heights = CatenaryHelper.calculateHeights(conn.value.xShift, conn.value.yShift, hDiff, conn.value.coefficient, 10)

                val m: Float = if ((vertexB.z - vertexA.z) != 0f) {
                    ((vertexB.x - vertexA.x) / (vertexB.z - vertexA.z))
                }
                else {
                    0f
                }
                val ht = hDiff / heights.size
                var dz = ht / (sqrt(m.pow(2) + 1))
                var dx = m * dz
                
                val angle = atan2(vertexB.x - vertexA.x, vertexB.z - vertexA.z) * (180 / PI)

                when {
                    angle >= 91.0
                 || angle <= -91.0        -> { dz = -dz; dx = -dx }
                    angle in 89.0..91.0   -> { dx = dz; dz = 0f }
                    angle in -91.0..-89.0 -> { dx = -dz; dz = 0f }
                }

                val colour = conn.value.wireColour

                for ((i, v) in heights.withIndex()) {
                    val bya = if (i == heights.lastIndex) {
                        vertexB.y
                    }
                    else {
                        heights[i + 1] - vertexA.y + offsets.y.toFloat()
                    }
                    val ayb = v - vertexA.y + offsets.y.toFloat()
                    val aya = ayb - conn.value.wireThickness
                    val byb = bya - conn.value.wireThickness
                    val ax = vertexA.x + i*dx
                    val az = vertexA.z + i*dz
                    val bx = vertexA.x + (i+1)*dx
                    val bz = vertexA.z + (i+1)*dz
                    vertices.vertex(matrices.peek().model, ax, aya, az)
                            .color(colour[0], colour[1], colour[2], 255)
                            .light(light)
                            .next()
                    vertices.vertex(matrices.peek().model, ax, ayb, az)
                            .color(colour[0], colour[1], colour[2], 255)
                            .light(light)
                            .next()
                    vertices.vertex(matrices.peek().model, bx, bya, bz)
                            .color(colour[0], colour[1], colour[2], 255)
                            .light(light)
                            .next()
                    vertices.vertex(matrices.peek().model, bx, byb, bz)
                            .color(colour[0], colour[1], colour[2], 255)
                            .light(light)
                            .next()
                }

                for ((i, v) in heights.withIndex()) {
                    val bya = if (i == heights.lastIndex) {
                        vertexB.y
                    }
                    else {
                        heights[i + 1] - vertexA.y + offsets.y.toFloat()
                    }
                    val ayb = v - vertexA.y + (offsets.y).toFloat()
                    val aya = ayb - conn.value.wireThickness / 2
                    val byb = bya - conn.value.wireThickness / 2
                    val ax = vertexA.x + i * dx
                    val az = vertexA.z + i * dz
                    val bx = vertexA.x + (i+1)*dx
                    val bz = vertexA.z + (i+1)*dz

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

                    vertices.vertex(matrices.peek().model, aa.x, aa.y, aa.z)
                            .color(colour[0], colour[1], colour[2], 255)
                            .light(light)
                            .next()
                    vertices.vertex(matrices.peek().model, ab.x, ab.y, ab.z)
                            .color(colour[0], colour[1], colour[2], 255)
                            .light(light)
                            .next()
                    vertices.vertex(matrices.peek().model, ba.x, ba.y, ba.z)
                            .color(colour[0], colour[1], colour[2], 255)
                            .light(light)
                            .next()
                    vertices.vertex(matrices.peek().model, bb.x, bb.y, bb.z)
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
