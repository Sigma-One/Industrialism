package sigmaone.industrialism

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricMaterialBuilder
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.block.MaterialColor
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolMaterials
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import sigmaone.industrialism.block.BlockConnectorDummy
import sigmaone.industrialism.block.BlockTater
import sigmaone.industrialism.block.machine.BlockBattery
import sigmaone.industrialism.block.machine.BlockEntityBattery
import sigmaone.industrialism.block.machine.BlockEntityManualGenerator
import sigmaone.industrialism.block.machine.BlockManualGenerator
import sigmaone.industrialism.block.multiblock.BlockEntityMultiblockChildBase
import sigmaone.industrialism.block.multiblock.BlockEntityMultiblockRootBase
import sigmaone.industrialism.block.multiblock.BlockMultiblockChildBase
import sigmaone.industrialism.block.multiblock.BlockMultiblockRootBase
import sigmaone.industrialism.block.multiblock.machine.BlockCokeOvenMultiblock
import sigmaone.industrialism.block.multiblock.machine.BlockEntityCokeOvenMultiblock
import sigmaone.industrialism.block.wiring.BlockEntityWireNode
import sigmaone.industrialism.block.wiring.BlockWireConnectorT0
import sigmaone.industrialism.item.ItemScrewdriver
import sigmaone.industrialism.item.ItemWireSpool
import sigmaone.industrialism.item.ItemWrench
import sigmaone.industrialism.material.metal.Metal
import sigmaone.industrialism.util.RegistryHelper.registerBlock
import sigmaone.industrialism.util.RegistryHelper.registerItem
import kotlin.collections.HashSet

class Industrialism : ModInitializer {
    enum class InputConfig {
        NONE, INPUT, OUTPUT
    }

    companion object {
        const val MOD_ID = "industrialism"

        // Materials creative tab
        private val materialsTabBuilder: FabricItemGroupBuilder = FabricItemGroupBuilder.create(Identifier(MOD_ID, "materials"))
        var MATERIALS_TAB: ItemGroup = materialsTabBuilder.icon { ItemStack(COPPER.ingot) }.build()

        // Tools creative tab
        private val toolsTabBuilder: FabricItemGroupBuilder = FabricItemGroupBuilder.create(Identifier(MOD_ID, "tools"))
        var TOOLS_TAB: ItemGroup = toolsTabBuilder.icon { ItemStack(COPPER.pickaxe) }.build()

        // Machines creative tab
        private val machinesTabBuilder: FabricItemGroupBuilder = FabricItemGroupBuilder.create(Identifier(MOD_ID, "machines"))
        var MACHINES_TAB: ItemGroup = machinesTabBuilder.icon { ItemStack(BATTERY_BLOCK) }.build()

        // Debug creative tab
        private val debugTabBuilder: FabricItemGroupBuilder = FabricItemGroupBuilder.create(Identifier(MOD_ID, "debug"))
        var DEBUG_TAB: ItemGroup = debugTabBuilder.icon { ItemStack(DEBUG_LINKER) }.build()

        // Materials to fix mining levels
        val MATERIAL_STONE: Material = FabricMaterialBuilder(MaterialColor.STONE).build()
        val MATERIAL_METAL: Material = FabricMaterialBuilder(MaterialColor.GRAY).build()

        // Metals
        val COPPER: Metal = Metal("copper")
                .addIngot()
                .addNugget()
                .addStick()
                .addBlock(1, 4.5f)
                .addOre("malachite", 1, 14, 8, 40, 64)
                .addToolMaterial(3, 192, 2, 5.0f, 16)
                .addAxe(6f, 0.9f)
                .addPickaxe(1, 1.2f)
                .addShovel(1.5f, 1.0f)
                .addSword(3, 1.6f)
                .addHoe(-2, 3.0f)
        val LEAD: Metal = Metal("lead")
                .addIngot()
                .addNugget()
                .addStick()
                .addBlock(1, 4.0f)
                .addOre("galena", 1, 8, 6, 10, 40)
                .addToolMaterial(2, 135, 1, 3.0f, 7)
                .addAxe(6f, 0.8f)
                .addPickaxe(1, 1.1f)
                .addShovel(1.5f, 0.9f)
                .addSword(3, 1.5f)
                .addHoe(-2, 2.9f)
        val IRON: Metal = Metal("iron")
                .addStick()
        val GOLD: Metal = Metal("gold")
                .addStick()

        // Single block machines
        val BATTERY_BLOCK: Block = registerBlock("basic_battery", BlockBattery(FabricBlockSettings.of(MATERIAL_METAL).hardness(3.0f)), MACHINES_TAB)
        val BATTERY: BlockEntityType<BlockEntityBattery> = Registry.register(Registry.BLOCK_ENTITY_TYPE, "$MOD_ID:basic_battery", BlockEntityType.Builder.create({ BlockEntityBattery() }, BATTERY_BLOCK).build(null))
        val MANUAL_GENERATOR_BLOCK: Block = registerBlock("manual_generator", BlockManualGenerator(FabricBlockSettings.of(MATERIAL_METAL).hardness(3.0f)), MACHINES_TAB)
        val MANUAL_GENERATOR: BlockEntityType<BlockEntityManualGenerator> = Registry.register(Registry.BLOCK_ENTITY_TYPE, "$MOD_ID:manual_generator", BlockEntityType.Builder.create({ BlockEntityManualGenerator() }, MANUAL_GENERATOR_BLOCK).build(null))

        // Wiring
        val CONNECTOR_T0_BLOCK: Block = registerBlock("connector_t0", BlockWireConnectorT0(FabricBlockSettings.of(Material.STONE).hardness(1.0f)), MACHINES_TAB)
        val CONNECTOR_T0: BlockEntityType<BlockEntityWireNode> = Registry.register(Registry.BLOCK_ENTITY_TYPE, "$MOD_ID:connector_t0", BlockEntityType.Builder.create({ BlockEntityWireNode() }, CONNECTOR_T0_BLOCK).build(null))

        // Utility items
        val SCREWDRIVER: Item = registerItem("screwdriver", ItemScrewdriver(ToolMaterials.IRON, 0, 3.0f, Item.Settings().group(TOOLS_TAB).maxCount(1)))
        val WRENCH: Item = registerItem("wrench", ItemWrench(ToolMaterials.IRON, 4, -3.0f, Item.Settings().group(TOOLS_TAB).maxCount(1)))
        val DEBUG_LINKER: Item = registerItem("debug_linker", ItemWireSpool(Item.Settings().group(DEBUG_TAB)))

        // Multiblock parts
        val MULTIBLOCK_CHILD_BLOCK: Block = Registry.register(
                Registry.BLOCK,
                Identifier(MOD_ID, "multiblock_child"),
                BlockMultiblockChildBase(FabricBlockSettings.of(MATERIAL_STONE).hardness(2.0f).nonOpaque()
                )
        )
        val MULTIBLOCK_CHILD: BlockEntityType<BlockEntityMultiblockChildBase> = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                "$MOD_ID:multiblock_child",
                BlockEntityType.Builder.create({ BlockEntityMultiblockChildBase() }, MULTIBLOCK_CHILD_BLOCK).build(null)
        )

        // Multiblocks
        val MULTIBLOCKS: HashSet<BlockMultiblockRootBase> = HashSet()
        val COKE_OVEN_MULTIBLOCK_BLOCK: BlockMultiblockRootBase = Registry.register(
                Registry.BLOCK,
                Identifier(MOD_ID, "coke_oven_multiblock"),
                BlockCokeOvenMultiblock(FabricBlockSettings.of(MATERIAL_STONE).hardness(2.0f)
                )
        )
        val COKE_OVEN_MULTIBLOCK: BlockEntityType<BlockEntityCokeOvenMultiblock> = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                "$MOD_ID:coke_oven_multiblock",
                BlockEntityType.Builder.create({ BlockEntityCokeOvenMultiblock() }, COKE_OVEN_MULTIBLOCK_BLOCK).build(null)
        )

        // Dummy blocks for rendering etc.
        val CONNECTOR_DUMMY: Block = Registry.register(Registry.BLOCK, Identifier(MOD_ID, "connector_dummy"), BlockConnectorDummy(FabricBlockSettings.of(Material.AIR)))

        // Misc
        val TATER: Block = registerBlock("tater", BlockTater(FabricBlockSettings.of(MATERIAL_STONE).hardness(2.0f)), TOOLS_TAB)
    }

    init {
        MULTIBLOCKS.add(COKE_OVEN_MULTIBLOCK_BLOCK)
    }

    override fun onInitialize() {
        // :tiny_potato:
    }
}