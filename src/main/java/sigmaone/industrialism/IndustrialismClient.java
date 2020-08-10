package sigmaone.industrialism;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import sigmaone.industrialism.block.BlockEntitySidedEnergyContainerRenderer;

@Environment(EnvType.CLIENT)
public class IndustrialismClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(Industrialism.BATTERY_BLOCK, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(Industrialism.CONNECTOR_DUMMY, RenderLayer.getCutout());
		BlockEntityRendererRegistry.INSTANCE.register(Industrialism.BATTERY, BlockEntitySidedEnergyContainerRenderer::new);
	}
}
