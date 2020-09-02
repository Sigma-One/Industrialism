package sigmaone.industrialism.block.wiring;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.*;


public class WireRenderer<T extends BlockEntityWireNode> extends BlockEntityRenderer<T> {
    public WireRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        for (BlockPos conn : entity.connections) {
            if (entity.getWorld().getBlockEntity(conn) != null && entity.getWorld().getBlockEntity(conn) instanceof BlockEntityWireNode) {

                VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getLines());

                float[] vertexAOffsets;
                float[] vertexBOffsets;
                float[] vertexBBase = new float[] {conn.getX() - entity.getPos().getX(), conn.getY() - entity.getPos().getY(), conn.getZ() - entity.getPos().getZ()};

                // @formatter:off
                switch (entity.getWorld().getBlockState(entity.getPos()).get(Properties.FACING)) {
                    case UP   : vertexAOffsets = new float[] {0.5f, (1f/16f)*4f, 0.5f }; break;
                    case DOWN : vertexAOffsets = new float[] {0.5f, (1f/16f)*12f, 0.5f}; break;
                    case NORTH: vertexAOffsets = new float[] {0.5f, 0.5f, (1f/16f)*12f}; break;
                    case SOUTH: vertexAOffsets = new float[] {0.5f, 0.5f, (1f/16f)*4f }; break;
                    case EAST : vertexAOffsets = new float[] {(1f/16f)*4f, 0.5f, 0.5f }; break;
                    case WEST : vertexAOffsets = new float[] {(1f/16f)*12f, 0.5f, 0.5f}; break;
                    default: throw new IllegalStateException("Illegal Facing");
                }

                switch (entity.getWorld().getBlockState(conn).get(Properties.FACING)) {
                    case UP   : vertexBOffsets = new float[] {0.5f, (1f/16f)*4f, 0.5f }; break;
                    case DOWN : vertexBOffsets = new float[] {0.5f, (1f/16f)*12f, 0.5f}; break;
                    case NORTH: vertexBOffsets = new float[] {0.5f, 0.5f, (1f/16f)*12f}; break;
                    case SOUTH: vertexBOffsets = new float[] {0.5f, 0.5f, (1f/16f)*4f }; break;
                    case EAST : vertexBOffsets = new float[] {(1f/16f)*4f, 0.5f, 0.5f }; break;
                    case WEST : vertexBOffsets = new float[] {(1f/16f)*12f, 0.5f, 0.5f}; break;
                    default: throw new IllegalStateException("Illegal Facing");
                }
                // @formatter:on

                float[] vertexA = new float[] {vertexAOffsets[0], vertexAOffsets[1], vertexAOffsets[2]};
                float[] vertexB = new float[] {vertexBBase[0] + vertexBOffsets[0], vertexBBase[1] + vertexBOffsets[1], vertexBBase[2] + vertexBOffsets[2]};

                vertices.vertex(matrices.peek().getModel(), vertexA[0], vertexA[1], vertexA[2]).color(100, 50, 10, 255).next();
                vertices.vertex(matrices.peek().getModel(), vertexB[0], vertexB[1], vertexB[2]).color(100, 50, 10, 255).next();
            }
        }
        matrices.pop();
    }
    @Override
    public boolean rendersOutsideBoundingBox(BlockEntityWireNode blockEntity) {
        return true;
    }
}
