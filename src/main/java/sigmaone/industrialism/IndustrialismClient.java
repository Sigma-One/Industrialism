package sigmaone.industrialism;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import sigmaone.industrialism.block.BlockEntitySidedEnergyContainerRenderer;
import sigmaone.industrialism.block.wiring.WireRenderer;

@Environment(EnvType.CLIENT)
public class IndustrialismClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(Industrialism.BATTERY_BLOCK, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(Industrialism.CONNECTOR_DUMMY, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(Industrialism.TATER, RenderLayer.getCutout());
		BlockEntityRendererRegistry.INSTANCE.register(Industrialism.BATTERY, BlockEntitySidedEnergyContainerRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(Industrialism.CONNECTOR_T0, WireRenderer::new);
		//BlockEntityRendererRegistry.INSTANCE.register(Industrialism.CONNECTOR_T1, WireRenderer::new);
		//BlockEntityRendererRegistry.INSTANCE.register(Industrialism.CONNECTOR_T2, WireRenderer::new);
	}
}
