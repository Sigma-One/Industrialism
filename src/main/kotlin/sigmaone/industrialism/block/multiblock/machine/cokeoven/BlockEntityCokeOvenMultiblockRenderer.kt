package sigmaone.industrialism.block.multiblock.machine.cokeoven

import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction
import sigmaone.industrialism.IndustrialismClient
import java.lang.IllegalStateException
import java.util.*


class BlockEntityCokeOvenMultiblockRenderer<T : BlockEntityCokeOvenMultiblock?>(dispatcher: BlockEntityRenderDispatcher?) : BlockEntityRenderer<T>(dispatcher) {
    override fun render(entity: T, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        val blockState = entity!!.world!!.getBlockState(entity.pos)
        val model: BakedModel

        model = if (blockState.get(Properties.LIT)) {
            MinecraftClient.getInstance().bakedModelManager.getModel(IndustrialismClient.cokeOvenModelIDs[1])
        }
        else {
            MinecraftClient.getInstance().bakedModelManager.getModel(IndustrialismClient.cokeOvenModelIDs[0])
        }

        matrices.push()

        when (blockState.get(Properties.HORIZONTAL_FACING)) {
            Direction.NORTH -> { matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(0f));   matrices.translate(0.0, 0.0, 1.0) }
            Direction.SOUTH -> { matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180f)); matrices.translate(-1.0, 0.0, 0.0) }
            Direction.EAST  -> { matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270f)) }
            Direction.WEST  -> { matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90f));  matrices.translate(-1.0, 0.0, 1.0) }
            else -> throw IllegalStateException("Invalid direction")
        }

        MinecraftClient.getInstance().blockRenderManager.modelRenderer.render(
                entity.world,
                model,
                blockState,
                entity.pos,
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getSolid()),
                false,
                Random(),
                System.currentTimeMillis(),
                overlay
        )
        matrices.pop()
    }
}