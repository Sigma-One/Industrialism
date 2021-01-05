package sigmaone.industrialism.block.crankhandle

import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.component.mechanical.IComponentMechanicalDevice
import java.util.*

class BlockEntityCrankHandleRenderer<T: BlockEntity>(dispatcher: BlockEntityRenderDispatcher?) : BlockEntityRenderer<T>(dispatcher) {
    override fun render(entity: T, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        if (entity is IComponentMechanicalDevice) {
            matrices.push()
            val deg = entity.componentMechanicalDevice.visualDegrees
            matrices.translate(0.5, 0.0, 0.5)
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(deg.toFloat()))
            matrices.translate(-(1/16.0), 0.0, -(1/16.0))

            val state = Industrialism.CRANK_HANDLE_DUMMY.defaultState

            MinecraftClient.getInstance().blockRenderManager.renderBlock(state, entity.pos, entity.world, matrices, vertexConsumers.getBuffer(
                RenderLayer.getCutout()), false, Random()
            )
            matrices.pop()
        }
    }
}