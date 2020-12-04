package sigmaone.industrialism.block

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties.FACING
import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.component.energy.IComponentEnergyContainer
import sigmaone.industrialism.util.IO
import java.util.*

class EnergyContainerRenderer <T: BlockEntity>(dispatcher: BlockEntityRenderDispatcher?) : BlockEntityRenderer<T>(dispatcher) {
    override fun render(entity: T, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        if (entity is IComponentEnergyContainer) {
            matrices.push()
            for ((i, dir) in Direction.values().withIndex()) {

                val conf = entity.componentEnergyContainer.sideConfig[dir]

                val state = when (conf) {
                    IO.NONE   -> Industrialism.CONNECTOR_DUMMY.defaultState.with(FACING, dir).with(IntProperty.of("state", 0, 2), 0)
                    IO.INPUT  -> Industrialism.CONNECTOR_DUMMY.defaultState.with(FACING, dir).with(IntProperty.of("state", 0, 2), 1)
                    IO.OUTPUT -> Industrialism.CONNECTOR_DUMMY.defaultState.with(FACING, dir).with(IntProperty.of("state", 0, 2), 2)
                    else      -> throw IllegalStateException("Invalid IO state")
                }
                MinecraftClient.getInstance().blockRenderManager.renderBlock(state, entity.pos, entity.world, matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, Random())
            }
            matrices.pop()
        }
    }
}