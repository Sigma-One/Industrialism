package sigmaone.industrialism.block;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import sigmaone.industrialism.Industrialism;

import java.util.Random;

public class BlockEntitySidedEnergyContainerRenderer<T extends BlockEntitySidedEnergyContainer> extends BlockEntityRenderer<T> {
    public BlockEntitySidedEnergyContainerRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        BlockState state;
        for (int i = 0; i <= 5; i++) {
            Direction dir;
            // @formatter off
            switch (i) {
                case 0: dir = Direction.NORTH; break;
                case 1: dir = Direction.EAST;  break;
                case 2: dir = Direction.SOUTH; break;
                case 3: dir = Direction.WEST;  break;
                case 4: dir = Direction.UP;    break;
                case 5: dir = Direction.DOWN;  break;
                default: throw new IllegalStateException("Loop machine br0ke");
            }
            // @formatter on
            Industrialism.SideEnergyConfig conf = entity.getSideConfig(dir);
            // @formatter off
            switch (conf) {
                case NONE:   state = Industrialism.CONNECTOR_DUMMY.getStateManager().getStates().get(i * 3    ); break;
                case INPUT:  state = Industrialism.CONNECTOR_DUMMY.getStateManager().getStates().get(i * 3 + 1); break;
                case OUTPUT: state = Industrialism.CONNECTOR_DUMMY.getStateManager().getStates().get(i * 3 + 2); break;
                default: throw new IllegalStateException("Illegal Side Configuration: " + conf);
            }
            // @formatter on
            MinecraftClient.getInstance().getBlockRenderManager().renderBlock(state, entity.getPos(), entity.getWorld(), matrices, vertexConsumers.getBuffer(RenderLayer.getCutout()), false, new Random());
        }
        matrices.pop();
    }
}
