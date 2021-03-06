package sigmaone.industrialism

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.resource.ResourceManager
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import sigmaone.industrialism.Industrialism.BATTERY
import sigmaone.industrialism.Industrialism.BATTERY_BLOCK
import sigmaone.industrialism.Industrialism.BLAST_FURNACE_MULTIBLOCK
import sigmaone.industrialism.Industrialism.BLAST_FURNACE_MULTIBLOCK_BLOCK
import sigmaone.industrialism.Industrialism.COKE_OVEN_MULTIBLOCK
import sigmaone.industrialism.Industrialism.COKE_OVEN_MULTIBLOCK_BLOCK
import sigmaone.industrialism.Industrialism.CONNECTOR_DUMMY
import sigmaone.industrialism.Industrialism.CONNECTOR_T0
import sigmaone.industrialism.Industrialism.CRANK_HANDLE
import sigmaone.industrialism.Industrialism.CRANK_HANDLE_BLOCK
import sigmaone.industrialism.Industrialism.MULTIBLOCK_CHILD_BLOCK
import sigmaone.industrialism.Industrialism.TATER
import sigmaone.industrialism.block.EnergyContainerRenderer
import sigmaone.industrialism.block.crankhandle.BlockEntityCrankHandleRenderer
import sigmaone.industrialism.block.multiblock.blastfurnace.BlastFurnaceGui
import sigmaone.industrialism.block.multiblock.blastfurnace.BlastFurnaceGuiDescription
import sigmaone.industrialism.block.multiblock.blastfurnace.BlockEntityBlastFurnaceMultiblockRenderer
import sigmaone.industrialism.block.multiblock.cokeoven.BlockEntityCokeOvenMultiblockRenderer
import sigmaone.industrialism.block.multiblock.cokeoven.CokeOvenGui
import sigmaone.industrialism.block.multiblock.cokeoven.CokeOvenGuiDescription
import sigmaone.industrialism.block.wiring.WireRenderer
import sigmaone.industrialism.util.datagen.DataGenerator
import java.util.function.Consumer

@Environment(EnvType.CLIENT)
object IndustrialismClient : ClientModInitializer {
    val cokeOvenModelIDs: Array<ModelIdentifier> = arrayOf(
            ModelIdentifier(Identifier(Industrialism.MOD_ID, "coke_oven_off"), ""),
            ModelIdentifier(Identifier(Industrialism.MOD_ID, "coke_oven_on"), "")
    )

    val blastFurnaceModelIDs: Array<ModelIdentifier> = arrayOf(
            ModelIdentifier(Identifier(Industrialism.MOD_ID, "blast_furnace_off"), ""),
            ModelIdentifier(Identifier(Industrialism.MOD_ID, "blast_furnace_on"), "")
    )

    override fun onInitializeClient() {
        DataGenerator.commitClient()

        ModelLoadingRegistry.INSTANCE.registerAppender { _: ResourceManager, ids: Consumer<ModelIdentifier> ->
            run {
                ids.accept(cokeOvenModelIDs[0])
                ids.accept(cokeOvenModelIDs[1])

                ids.accept(blastFurnaceModelIDs[0])
                ids.accept(blastFurnaceModelIDs[1])
            }
        }

        BlockRenderLayerMap.INSTANCE.putBlock(BATTERY_BLOCK, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(CONNECTOR_DUMMY, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(TATER, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(MULTIBLOCK_CHILD_BLOCK, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(COKE_OVEN_MULTIBLOCK_BLOCK, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(BLAST_FURNACE_MULTIBLOCK_BLOCK, RenderLayer.getCutout())
        BlockRenderLayerMap.INSTANCE.putBlock(CRANK_HANDLE_BLOCK, RenderLayer.getCutout())
        BlockEntityRendererRegistry.INSTANCE.register(BATTERY) { dispatcher: BlockEntityRenderDispatcher? -> EnergyContainerRenderer(dispatcher) }
        BlockEntityRendererRegistry.INSTANCE.register(CONNECTOR_T0) { dispatcher: BlockEntityRenderDispatcher? -> WireRenderer(dispatcher) }
        BlockEntityRendererRegistry.INSTANCE.register(BLAST_FURNACE_MULTIBLOCK) { dispatcher: BlockEntityRenderDispatcher? -> BlockEntityBlastFurnaceMultiblockRenderer(dispatcher) }
        BlockEntityRendererRegistry.INSTANCE.register(COKE_OVEN_MULTIBLOCK) { dispatcher: BlockEntityRenderDispatcher? -> BlockEntityCokeOvenMultiblockRenderer(dispatcher) }
        BlockEntityRendererRegistry.INSTANCE.register(CRANK_HANDLE) { dispatcher: BlockEntityRenderDispatcher? -> BlockEntityCrankHandleRenderer(dispatcher) }
        //BlockEntityRendererRegistry.INSTANCE.register(Industrialism.CONNECTOR_T1, WireRenderer::new);
        //BlockEntityRendererRegistry.INSTANCE.register(Industrialism.CONNECTOR_T2, WireRenderer::new);
        ScreenRegistry.register(
                Industrialism.COKE_OVEN_SCREEN_HANDLER_TYPE,
                { gui: CokeOvenGuiDescription, inventory: PlayerInventory, title: Text -> CokeOvenGui(gui, inventory.player, title) }
        )
        ScreenRegistry.register(
                Industrialism.BLAST_FURNACE_SCREEN_HANDLER_TYPE,
                { gui: BlastFurnaceGuiDescription, inventory: PlayerInventory, title: Text -> BlastFurnaceGui(gui, inventory.player, title) }
        )
    }
}