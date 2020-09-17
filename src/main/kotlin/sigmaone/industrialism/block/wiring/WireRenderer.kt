package sigmaone.industrialism.block.wiring

import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction

class WireRenderer<T : BlockEntityWireNode>(dispatcher: BlockEntityRenderDispatcher?) : BlockEntityRenderer<T>(dispatcher) {
    override fun render(entity: T, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        matrices.push()
        for (conn in entity.connections) {
            if (entity.world!!.getBlockEntity(conn) != null && entity.world!!.getBlockEntity(conn) is BlockEntityWireNode) {
                val vertices = vertexConsumers.getBuffer(RenderLayer.getLines())
                val vertexBBase = floatArrayOf(
                        conn.x - entity.pos.x.toFloat(),
                        conn.y - entity.pos.y.toFloat(),
                        conn.z - entity.pos.z.toFloat()
                )

                val vertexAOffsets: FloatArray = when (entity.world!!.getBlockState(entity.pos).get(Properties.FACING)) {
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
                }

                val vertexA = floatArrayOf(vertexAOffsets[0], vertexAOffsets[1], vertexAOffsets[2])
                val vertexB = floatArrayOf(vertexBBase[0] + vertexBOffsets[0], vertexBBase[1] + vertexBOffsets[1], vertexBBase[2] + vertexBOffsets[2])
                vertices.vertex(matrices.peek().model, vertexA[0], vertexA[1], vertexA[2]).color(100, 50, 10, 255).next()
                vertices.vertex(matrices.peek().model, vertexB[0], vertexB[1], vertexB[2]).color(100, 50, 10, 255).next()
            }
        }
        matrices.pop()
    }

    override fun rendersOutsideBoundingBox(blockEntity: T): Boolean {
        return true
    }
}