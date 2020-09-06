package sigmaone.industrialism.block.multiblock.machine;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import sigmaone.industrialism.Industrialism;

import java.util.Random;

public class BlockEntityTestMultiblockRenderer extends BlockEntityRenderer<BlockEntityTestMultiblock> {
    public BlockEntityTestMultiblockRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(BlockEntityTestMultiblock entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.scale(2, 2, 2);
        MinecraftClient.getInstance().getBlockRenderManager().renderBlock(Industrialism.TEST_MULTIBLOCK_DUMMY.getDefaultState(), entity.getPos(), entity.getWorld(), matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, new Random());
        matrices.pop();
    }
}
