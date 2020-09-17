package sigmaone.industrialism.block

import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.Industrialism.InputConfig
import java.util.*

class BlockEntitySidedEnergyContainerRenderer<T : BlockEntitySidedEnergyContainer?>(dispatcher: BlockEntityRenderDispatcher?) : BlockEntityRenderer<T>(dispatcher) {
    override fun render(entity: T, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        matrices.push()
        var state: BlockState?
        var dir: Direction
        for (i in 0..5) {
            dir = when (i) {
                0 -> Direction.NORTH
                1 -> Direction.EAST
                2 -> Direction.SOUTH
                3 -> Direction.WEST
                4 -> Direction.UP
                5 -> Direction.DOWN
                else -> throw IllegalStateException("Loop machine br0ke")
            }

            val conf = entity!!.sideConfig[dir]

            state = when (conf) {
                InputConfig.NONE   -> Industrialism.CONNECTOR_DUMMY.stateManager.states[i * 3]
                InputConfig.INPUT  -> Industrialism.CONNECTOR_DUMMY.stateManager.states[i * 3 + 1]
                InputConfig.OUTPUT -> Industrialism.CONNECTOR_DUMMY.stateManager.states[i * 3 + 2]
                else -> throw IllegalStateException("Unexpected value: $conf")
            }
            MinecraftClient.getInstance().blockRenderManager.renderBlock(state, entity.pos, entity.world, matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, Random())
        }
        matrices.pop()
    }
}