package sigmaone.industrialism.block.crankhandle

import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.component.mechanical.IComponentMechanicalDevice

class BlockEntityCrankHandleRenderer<T: BlockEntity>(dispatcher: BlockEntityRenderDispatcher?) : BlockEntityRenderer<T>(dispatcher) {
    override fun render(entity: T, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        if (entity is IComponentMechanicalDevice) {
            matrices.push()
            val deg = entity.componentMechanicalDevice.visualDegrees
            val axis: Vector3f
            when(entity.world!!.getBlockState(entity.pos).get(Properties.FACING)) {
                Direction.DOWN  -> {
                    axis = Vector3f.NEGATIVE_Y
                    matrices.translate(0.5, 1.0, 0.5)
                    matrices.multiply(axis.getDegreesQuaternion(deg.toFloat()))
                    matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180f))
                }
                Direction.UP    -> {
                    axis = Vector3f.POSITIVE_Y
                    matrices.translate(0.5, 0.0, 0.5)
                    matrices.multiply(axis.getDegreesQuaternion(deg.toFloat()))
                }
                Direction.NORTH -> {
                    axis = Vector3f.NEGATIVE_Z
                    matrices.translate(0.5, 0.5, 1.0)
                    matrices.multiply(axis.getDegreesQuaternion(deg.toFloat()))
                    matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(270f))
                }
                Direction.SOUTH -> {
                    axis = Vector3f.POSITIVE_Z
                    matrices.translate(0.5, 0.5, 0.0)
                    matrices.multiply(axis.getDegreesQuaternion(deg.toFloat()))
                    matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90f))
                }
                Direction.WEST  -> {
                    axis = Vector3f.NEGATIVE_X
                    matrices.translate(1.0, 0.5, 0.5)
                    matrices.multiply(axis.getDegreesQuaternion(deg.toFloat()))
                    matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90f))
                }
                Direction.EAST  -> {
                    axis = Vector3f.POSITIVE_X
                    matrices.translate(0.0, 0.5, 0.5)
                    matrices.multiply(axis.getDegreesQuaternion(deg.toFloat()))
                    matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(270f))
                }
                else            -> throw IllegalStateException("Invalid facing")
            }
            matrices.translate(-(1/16.0), 0.0, -(1/16.0))

            val state = Industrialism.CRANK_HANDLE_DUMMY.defaultState

            //MinecraftClient.getInstance().blockRenderManager.renderBlock(state, entity.pos, entity.world, matrices, vertexConsumers.getBuffer(
            //    RenderLayer.getCutout()), false, Random()
            //)
            MinecraftClient.getInstance().blockRenderManager.renderBlockAsEntity(state, matrices, vertexConsumers, light, overlay)
            matrices.pop()
        }
    }
}