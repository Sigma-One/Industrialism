package sigmaone.industrialism

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import sigmaone.industrialism.Industrialism.Companion.BATTERY
import sigmaone.industrialism.Industrialism.Companion.BATTERY_BLOCK
import sigmaone.industrialism.Industrialism.Companion.COKE_OVEN_MULTIBLOCK
import sigmaone.industrialism.Industrialism.Companion.COKE_OVEN_MULTIBLOCK_BLOCK
import sigmaone.industrialism.Industrialism.Companion.CONNECTOR_DUMMY
import sigmaone.industrialism.Industrialism.Companion.CONNECTOR_T0
import sigmaone.industrialism.Industrialism.Companion.MULTIBLOCK_CHILD_BLOCK
import sigmaone.industrialism.Industrialism.Companion.TATER
import sigmaone.industrialism.block.BlockEntitySidedEnergyContainerRenderer
import sigmaone.industrialism.block.multiblock.machine.BlockEntityCokeOvenMultiblockRenderer
import sigmaone.industrialism.block.wiring.WireRenderer
import java.util.function.Consumer

@Environment(EnvType.CLIENT)
class IndustrialismClient : ClientModInitializer {
    companion object {
        val cokeOvenModelIDs: Array<ModelIdentifier> = arrayOf(
                ModelIdentifier(Identifier(Industrialism.MOD_ID, "coke_oven_off"), ""),
                ModelIdentifier(Identifier(Industrialism.MOD_ID, "coke_oven_on"), "")
        )
    }

    override fun onInitializeClient() {

        ModelLoadingRegistry.INSTANCE.registerAppender { _: ResourceManager, ids: Consumer<ModelIdentifier> ->
            run {
                ids.accept(cokeOvenModelIDs[0])
                ids.accept(cokeOvenModelIDs[1])
            }
        }

        BlockRenderLayerMap.INSTANCE.putBlock(BATTERY_BLOCK, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(CONNECTOR_DUMMY, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(TATER, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(MULTIBLOCK_CHILD_BLOCK, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(COKE_OVEN_MULTIBLOCK_BLOCK, RenderLayer.getCutout())
        BlockEntityRendererRegistry.INSTANCE.register(BATTERY) { dispatcher: BlockEntityRenderDispatcher? -> BlockEntitySidedEnergyContainerRenderer(dispatcher) }
        BlockEntityRendererRegistry.INSTANCE.register(CONNECTOR_T0) { dispatcher: BlockEntityRenderDispatcher? -> WireRenderer(dispatcher) }
        BlockEntityRendererRegistry.INSTANCE.register(COKE_OVEN_MULTIBLOCK) { dispatcher: BlockEntityRenderDispatcher? -> BlockEntityCokeOvenMultiblockRenderer(dispatcher) }
        //BlockEntityRendererRegistry.INSTANCE.register(Industrialism.CONNECTOR_T1, WireRenderer::new);
        //BlockEntityRendererRegistry.INSTANCE.register(Industrialism.CONNECTOR_T2, WireRenderer::new);
        //BlockEntityRendererRegistry.INSTANCE.register(Industrialism.COKE_OVEN_MULTIBLOCK, BlockEntityCokeOvenMultiblockRenderer::new);
    }
}