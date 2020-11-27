package sigmaone.industrialism

import dev.onyxstudios.foml.obj.OBJLoader
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricMaterialBuilder
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.*
import net.minecraft.recipe.CookingRecipeSerializer
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.state.property.Properties
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import sigmaone.industrialism.block.BlockConnectorDummy
import sigmaone.industrialism.block.BlockTater
import sigmaone.industrialism.block.machine.BlockBattery
import sigmaone.industrialism.block.machine.BlockEntityBattery
import sigmaone.industrialism.block.machine.BlockEntityManualGenerator
import sigmaone.industrialism.block.machine.BlockManualGenerator
import sigmaone.industrialism.block.multiblock.BlockEntityMultiblockChildBase
import sigmaone.industrialism.block.multiblock.BlockMultiblockChildBase
import sigmaone.industrialism.block.multiblock.BlockMultiblockRootBase
import sigmaone.industrialism.block.multiblock.machine.blastfurnace.BlastFurnaceGuiDescription
import sigmaone.industrialism.block.multiblock.machine.blastfurnace.BlockBlastFurnaceMultiblock
import sigmaone.industrialism.block.multiblock.machine.blastfurnace.BlockEntityBlastFurnaceMultiblock
import sigmaone.industrialism.block.multiblock.machine.cokeoven.BlockCokeOvenMultiblock
import sigmaone.industrialism.block.multiblock.machine.cokeoven.BlockEntityCokeOvenMultiblock
import sigmaone.industrialism.block.multiblock.machine.cokeoven.CokeOvenGuiDescription
import sigmaone.industrialism.block.wiring.BlockEntityWireNode
import sigmaone.industrialism.block.wiring.BlockWireConnectorT0
import sigmaone.industrialism.item.ItemScrewdriver
import sigmaone.industrialism.item.ItemWireSpool
import sigmaone.industrialism.item.ItemWrench
import sigmaone.industrialism.material.metal.Metal
import sigmaone.industrialism.recipe.BlastingRecipe
import sigmaone.industrialism.recipe.CokingRecipe
import sigmaone.industrialism.util.RegistryHelper.registerBlock
import sigmaone.industrialism.util.RegistryHelper.registerItem

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
        val STEEL: Metal = Metal("steel")
                .addIngot()
                .addNugget()
                .addStick()
                .addBlock(2, 5.5f)
                .addToolMaterial(2, 500, 3, 7.0f, 6)
                .addAxe(6f, 0.9f)
                .addPickaxe(1, 1.2f)
                .addShovel(1.5f, 1.0f)
                .addSword(3, 1.6f)
                .addHoe(-2, 3.0f)
                .addArmour(
                        intArrayOf(247, 285, 304, 209), // Durabilities (Boots, Legs, Chest, Helmet)
                        intArrayOf(2, 5, 7, 2),         // Protection values
                        1.0f,
                        0.1f,
                        7
                )

        // Random materials
        val COKE: Item = registerItem("coke", Item(Item.Settings().group(MATERIALS_TAB)))
        val COKE_BLOCK = registerBlock("coke_block", Block(FabricBlockSettings.copyOf(Blocks.COAL_BLOCK)), FabricItemSettings().group(MATERIALS_TAB))

        // Intermediates
        val CERAMIC_DISK: Item = registerItem("ceramic_disk", Item(FabricItemSettings().group(MATERIALS_TAB)))
        val RAW_FIRE_BRICK = registerItem("raw_fire_brick", Item(Item.Settings().group(MATERIALS_TAB)))
        val FIRE_BRICK = registerItem("fire_brick", Item(Item.Settings().fireproof().group(MATERIALS_TAB)))

        val FIRE_BRICKS = registerBlock("fire_bricks", Block(FabricBlockSettings.copyOf(Blocks.BRICKS)), FabricItemSettings().group(MATERIALS_TAB).fireproof())

        // Single block machines
        val BATTERY_BLOCK: Block = registerBlock("basic_battery", BlockBattery(FabricBlockSettings.of(MATERIAL_METAL).hardness(3.0f)), FabricItemSettings().group(MACHINES_TAB))
        val BATTERY: BlockEntityType<BlockEntityBattery> = Registry.register(Registry.BLOCK_ENTITY_TYPE, "$MOD_ID:basic_battery", BlockEntityType.Builder.create({ BlockEntityBattery() }, BATTERY_BLOCK).build(null))
        val MANUAL_GENERATOR_BLOCK: Block = registerBlock("manual_generator", BlockManualGenerator(FabricBlockSettings.of(MATERIAL_METAL).hardness(3.0f)), FabricItemSettings().group(MACHINES_TAB))
        val MANUAL_GENERATOR: BlockEntityType<BlockEntityManualGenerator> = Registry.register(Registry.BLOCK_ENTITY_TYPE, "$MOD_ID:manual_generator", BlockEntityType.Builder.create({ BlockEntityManualGenerator() }, MANUAL_GENERATOR_BLOCK).build(null))

        // Wiring
        val CONNECTOR_T0_BLOCK: Block = registerBlock("connector_t0", BlockWireConnectorT0(FabricBlockSettings.of(Material.STONE).hardness(1.0f)), FabricItemSettings().group(MACHINES_TAB))
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
                BlockCokeOvenMultiblock(FabricBlockSettings.of(MATERIAL_STONE).hardness(2.0f).luminance { state -> if (state.get(Properties.LIT)) {7} else {0} }
                )
        )
        val COKE_OVEN_MULTIBLOCK: BlockEntityType<BlockEntityCokeOvenMultiblock> = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                "$MOD_ID:coke_oven_multiblock",
                BlockEntityType.Builder.create({ BlockEntityCokeOvenMultiblock() }, COKE_OVEN_MULTIBLOCK_BLOCK).build(null)
        )
        val COKE_OVEN_SCREEN_HANDLER_TYPE: ScreenHandlerType<CokeOvenGuiDescription> = ScreenHandlerRegistry.registerSimple(
                Identifier(MOD_ID, "coke_oven_multiblock"),
                { syncId, inventory -> CokeOvenGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY) }
        )

        val BLAST_FURNACE_MULTIBLOCK_BLOCK: BlockMultiblockRootBase = Registry.register(
                Registry.BLOCK,
                Identifier(MOD_ID, "blast_furnace_multiblock"),
                BlockBlastFurnaceMultiblock(FabricBlockSettings.of(MATERIAL_STONE).hardness(2.0f).luminance { state -> if (state.get(Properties.LIT)) {7} else {0} }
                )
        )
        val BLAST_FURNACE_MULTIBLOCK: BlockEntityType<BlockEntityBlastFurnaceMultiblock> = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                "$MOD_ID:blast_furnace_multiblock",
                BlockEntityType.Builder.create({ BlockEntityBlastFurnaceMultiblock() }, BLAST_FURNACE_MULTIBLOCK_BLOCK).build(null)
        )
        val BLAST_FURNACE_SCREEN_HANDLER_TYPE: ScreenHandlerType<BlastFurnaceGuiDescription> = ScreenHandlerRegistry.registerSimple(
                Identifier(MOD_ID, "blast_furnace_multiblock"),
                { syncId, inventory -> BlastFurnaceGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY) }
        )

        // Dummy blocks for rendering etc.
        val CONNECTOR_DUMMY: Block = Registry.register(Registry.BLOCK, Identifier(MOD_ID, "connector_dummy"), BlockConnectorDummy(FabricBlockSettings.of(Material.AIR)))

        // Misc
        val TATER: Block = registerBlock("tater", BlockTater(FabricBlockSettings.of(MATERIAL_STONE).hardness(2.0f)), FabricItemSettings().group(TOOLS_TAB))

        // Recipes
        val COKING_RECIPE_TYPE: RecipeType<CokingRecipe> = Registry.register(Registry.RECIPE_TYPE, Identifier(MOD_ID, "coking"), object: RecipeType<CokingRecipe> {})
        val COKING_RECIPE_SERIALIZER: RecipeSerializer<*> = Registry.register(Registry.RECIPE_SERIALIZER, Identifier(MOD_ID, "coking"), CookingRecipeSerializer(::CokingRecipe, 500))

        val BLASTING_RECIPE_TYPE: RecipeType<BlastingRecipe> = Registry.register(Registry.RECIPE_TYPE, Identifier(MOD_ID, "blasting"), object: RecipeType<BlastingRecipe> {})
        val BLASTING_RECIPE_SERIALIZER: RecipeSerializer<*> = Registry.register(Registry.RECIPE_SERIALIZER, Identifier(MOD_ID, "blasting"), CookingRecipeSerializer(::BlastingRecipe, 500))
    }

    init {
        OBJLoader.INSTANCE.registerDomain(MOD_ID)

        MULTIBLOCKS.add(COKE_OVEN_MULTIBLOCK_BLOCK)
        MULTIBLOCKS.add(BLAST_FURNACE_MULTIBLOCK_BLOCK)

        FuelRegistry.INSTANCE.add(COKE as ItemConvertible, 3200)
        FuelRegistry.INSTANCE.add(COKE_BLOCK as ItemConvertible, 32000)
    }

    override fun onInitialize() {
        // :tiny_potato:
    }
}