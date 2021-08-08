package sigmaone.industrialism.block

import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties.FACING
import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.component.energy.IComponentEnergyContainer
import sigmaone.industrialism.util.IO
import java.util.*

class EnergyContainerRenderer <T: BlockEntity> : BlockEntityRenderer<T> {
    override fun render(entity: T, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        if (entity is IComponentEnergyContainer) {
            matrices.push()
            for (dir in Direction.values()) {

                val conf = entity.componentEnergyContainer.sideConfig[dir]

                val state = when (conf) {
                    IO.NONE   -> Industrialism.CONNECTOR_DUMMY.defaultState.with(FACING, dir).with(IntProperty.of("state", 0, 2), 0)
                    IO.INPUT  -> Industrialism.CONNECTOR_DUMMY.defaultState.with(FACING, dir).with(IntProperty.of("state", 0, 2), 1)
                    IO.OUTPUT -> Industrialism.CONNECTOR_DUMMY.defaultState.with(FACING, dir).with(IntProperty.of("state", 0, 2), 2)
                    else      -> throw IllegalStateException("Invalid IO state")
                }
                matrices.scale(1.0001f, 1.0001f, 1.0001f)
                matrices.translate(-0.00005, -0.00005, -0.00005)
                MinecraftClient.getInstance().blockRenderManager.renderBlock(state, entity.pos, entity.world, matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, Random())
            }
            matrices.pop()
        }
    }
}