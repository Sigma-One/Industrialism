package sigmaone.industrialism.block.multiblock.machine;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.state.property.Properties;
import sigmaone.industrialism.Industrialism;

import java.util.Random;

public class BlockEntityTestMultiblockRenderer extends BlockEntityRenderer<BlockEntityTestMultiblock> {
    public BlockEntityTestMultiblockRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(BlockEntityTestMultiblock entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        switch(entity.getWorld().getBlockState(entity.getPos()).get(Properties.HORIZONTAL_FACING)) {
            case NORTH -> { matrices.translate(0, 0, -1); }
            case SOUTH -> { matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180)); matrices.translate(-1, 0, -2); }
            case EAST  -> { matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270)); matrices.translate(0, 0, -2);  }
            case WEST  -> { matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90));  matrices.translate(-1, 0, -1); }
        }
        matrices.scale(2, 2, 2);
        MinecraftClient.getInstance().getBlockRenderManager().renderBlock(Industrialism.TEST_MULTIBLOCK_DUMMY.getDefaultState(), entity.getPos(), entity.getWorld(), matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, new Random());
        matrices.pop();
    }
}
