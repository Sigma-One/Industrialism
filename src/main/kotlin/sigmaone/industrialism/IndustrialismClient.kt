package sigmaone.industrialism

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import sigmaone.industrialism.Industrialism.Companion.BATTERY
import sigmaone.industrialism.Industrialism.Companion.BATTERY_BLOCK
import sigmaone.industrialism.Industrialism.Companion.COKE_OVEN_MULTIBLOCK_BLOCK
import sigmaone.industrialism.Industrialism.Companion.CONNECTOR_DUMMY
import sigmaone.industrialism.Industrialism.Companion.CONNECTOR_T0
import sigmaone.industrialism.Industrialism.Companion.MULTIBLOCK_CHILD_BLOCK
import sigmaone.industrialism.Industrialism.Companion.TATER
import sigmaone.industrialism.block.BlockEntitySidedEnergyContainerRenderer
import sigmaone.industrialism.block.wiring.WireRenderer
import java.util.function.Function

@Environment(EnvType.CLIENT)
class IndustrialismClient : ClientModInitializer {
    //public static final ModelIdentifier CokeOvenNorthIdentifier = new ModelIdentifier(new Identifier(Industrialism.MOD_ID, "coke_oven"), "facing=north");
    override fun onInitializeClient() {

        /*ModelLoadingRegistry.INSTANCE.registerAppender((resourceManager, ids) -> {
			ids.accept(new ModelIdentifier(new Identifier(Industrialism.MOD_ID, "coke_oven"), "facing=north"));
			ids.accept(new ModelIdentifier(new Identifier(Industrialism.MOD_ID, "coke_oven"), "facing=south"));
			ids.accept(new ModelIdentifier(new Identifier(Industrialism.MOD_ID, "coke_oven"), "facing=east" ));
			ids.accept(new ModelIdentifier(new Identifier(Industrialism.MOD_ID, "coke_oven"), "facing=west" ));
		});*/
        BlockRenderLayerMap.INSTANCE.putBlock(BATTERY_BLOCK, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(CONNECTOR_DUMMY, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(TATER, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(MULTIBLOCK_CHILD_BLOCK, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(COKE_OVEN_MULTIBLOCK_BLOCK, RenderLayer.getCutout())
        BlockEntityRendererRegistry.INSTANCE.register(BATTERY, Function { dispatcher: BlockEntityRenderDispatcher? -> BlockEntitySidedEnergyContainerRenderer(dispatcher) })
        BlockEntityRendererRegistry.INSTANCE.register(CONNECTOR_T0, Function { dispatcher: BlockEntityRenderDispatcher? -> WireRenderer(dispatcher) })
        //BlockEntityRendererRegistry.INSTANCE.register(Industrialism.CONNECTOR_T1, WireRenderer::new);
        //BlockEntityRendererRegistry.INSTANCE.register(Industrialism.CONNECTOR_T2, WireRenderer::new);
        //BlockEntityRendererRegistry.INSTANCE.register(Industrialism.COKE_OVEN_MULTIBLOCK, BlockEntityCokeOvenMultiblockRenderer::new);
    }
}