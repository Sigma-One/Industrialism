package sigmaone.industrialism

import dev.onyxstudios.foml.obj.OBJLoader
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricMaterialBuilder
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.Material
import net.minecraft.block.MaterialColor
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.*
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.state.property.Properties
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import sigmaone.industrialism.block.BlockConnectorDummy
import sigmaone.industrialism.block.BlockTater
import sigmaone.industrialism.block.battery.BlockBattery
import sigmaone.industrialism.block.battery.BlockEntityBattery
import sigmaone.industrialism.block.crankhandle.BlockCrankHandle
import sigmaone.industrialism.block.crankhandle.BlockEntityCrankHandle
import sigmaone.industrialism.block.dynamo.BlockDynamo
import sigmaone.industrialism.block.dynamo.BlockEntityDynamo
import sigmaone.industrialism.block.multiblock.BlockEntityMultiblockChild
import sigmaone.industrialism.block.multiblock.BlockMultiblockChild
import sigmaone.industrialism.block.multiblock.BlockMultiblockRoot
import sigmaone.industrialism.block.multiblock.blastfurnace.BlastFurnaceGuiDescription
import sigmaone.industrialism.block.multiblock.blastfurnace.BlockBlastFurnaceMultiblock
import sigmaone.industrialism.block.multiblock.blastfurnace.BlockEntityBlastFurnaceMultiblock
import sigmaone.industrialism.block.multiblock.cokeoven.BlockCokeOvenMultiblock
import sigmaone.industrialism.block.multiblock.cokeoven.BlockEntityCokeOvenMultiblock
import sigmaone.industrialism.block.multiblock.cokeoven.CokeOvenGuiDescription
import sigmaone.industrialism.block.wiring.BlockEntityWireConnectorT0
import sigmaone.industrialism.block.wiring.BlockWireConnectorT0
import sigmaone.industrialism.item.ItemEngineersJournal
import sigmaone.industrialism.item.ItemScrewdriver
import sigmaone.industrialism.item.ItemWireSpool
import sigmaone.industrialism.item.ItemWrench
import sigmaone.industrialism.item.tool.ToolSword
import sigmaone.industrialism.material.metal.Metal
import sigmaone.industrialism.recipe.*
import sigmaone.industrialism.util.RegistryHelper.registerItem
import sigmaone.industrialism.util.content.RegisteredBlockBuilder
import sigmaone.industrialism.util.datagen.DataGenerator
import vazkii.patchouli.client.book.ClientBookRegistry

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
        val MATERIAL_WOOD : Material = FabricMaterialBuilder(MaterialColor.WOOD).build()

        // Utility items
        val SCREWDRIVER: Item = registerItem("screwdriver", ItemScrewdriver(ToolMaterials.IRON, 0, 3.0f, Item.Settings().group(TOOLS_TAB).maxCount(1)))
        val WRENCH: Item = registerItem("wrench", ItemWrench(ToolMaterials.IRON, 4, -3.0f, Item.Settings().group(TOOLS_TAB).maxCount(1)))
        val FORGE_HAMMER: Item = registerItem("forge_hammer", ToolSword(ToolMaterials.IRON, 5, -3.5f, Item.Settings().maxCount(1).group(TOOLS_TAB)))
        val ENGINEERS_JOURNAL: Item = registerItem("engineers_journal", ItemEngineersJournal(Item.Settings().group(TOOLS_TAB).maxCount(1)))

        // Metals
        var COPPER: Metal = Metal("copper")
            .addIngot()
            .addNugget()
            .addStick()
            .addPlate()
            .addWire(10, 0.05f, intArrayOf(220, 145, 85))
            .addBlock(1, 4.5f)
            .addOre("malachite", 1, 14, 8, 40, 64)
            .addToolMaterial(3f, 192, 2, 5.0f, 16)
            .addAxe(6f, 0.9f)
            .addPickaxe(1, 1.2f)
            .addShovel(1.5f, 1.0f)
            .addSword(3, 1.6f)
            .addHoe(-2, 3.0f)
        var LEAD: Metal = Metal("lead")
            .addIngot()
            .addNugget()
            .addStick()
            .addPlate()
            .addBlock(1, 4.0f)
            .addOre("galena", 1, 8, 6, 10, 40)
            .addToolMaterial(2f, 135, 1, 3.0f, 7)
            .addAxe(6f, 0.8f)
            .addPickaxe(1, 1.1f)
            .addShovel(1.5f, 0.9f)
            .addSword(3, 1.5f)
            .addHoe(-2, 2.9f)
        var IRON: Metal = Metal("iron")
            .setIngot(Items.IRON_INGOT)
            .setNugget(Items.IRON_NUGGET)
            .setBlock(Blocks.IRON_BLOCK)
            .setOre(Blocks.IRON_ORE)
            .addStick()
            .addPlate()
        var GOLD: Metal = Metal("gold")
            .setIngot(Items.GOLD_INGOT)
            .setNugget(Items.GOLD_NUGGET)
            .setBlock(Blocks.GOLD_BLOCK)
            .setOre(Blocks.GOLD_ORE)
            .addStick()
            .addPlate()
        var STEEL: Metal = Metal("steel")
            .addIngot()
            .addNugget()
            .addStick()
            .addPlate()
            .addBlock(2, 5.5f)
            .addToolMaterial(3.5f, 500, 3, 7.0f, 6)
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
        val COKE_BLOCK = RegisteredBlockBuilder(
            "coke_block",
            Block(FabricBlockSettings.copyOf(Blocks.COAL_BLOCK))
        )
            .withGeneratedModel()
            .withGeneratedBlockState()
            .withGeneratedItem(FabricItemSettings().group(MATERIALS_TAB))
            .build()

        // Intermediates
        val CERAMIC_DISC: Item = registerItem("ceramic_disc", Item(FabricItemSettings().group(MATERIALS_TAB)))
        val RAW_FIRE_BRICK = registerItem("raw_fire_brick", Item(Item.Settings().group(MATERIALS_TAB)))
        val FIRE_BRICK = registerItem("fire_brick", Item(Item.Settings().fireproof().group(MATERIALS_TAB)))

        val FIRE_BRICKS = RegisteredBlockBuilder(
            "fire_bricks",
            Block(FabricBlockSettings.copyOf(Blocks.NETHER_BRICKS))
        )
            .withGeneratedModel()
            .withGeneratedBlockState()
            .withGeneratedItem(FabricItemSettings().group(MATERIALS_TAB).fireproof())
            .build()

        val BRACED_FIRE_BRICKS = RegisteredBlockBuilder(
            "braced_fire_bricks",
            Block(FabricBlockSettings.copyOf(Blocks.NETHER_BRICKS).strength(2.5f, 6.5f))
        )
            .withGeneratedModel()
            .withGeneratedBlockState()
            .withGeneratedItem(FabricItemSettings().group(MATERIALS_TAB).fireproof())
            .build()

        // Single block machines
        // Battery
        val BATTERY_BLOCK: Block = RegisteredBlockBuilder(
            "basic_battery",
            BlockBattery(FabricBlockSettings.of(MATERIAL_METAL).hardness(3.0f))
        )
            .withSimpleSidedModel()
            .withGeneratedBlockState()
            .withGeneratedItem(FabricItemSettings().group(MACHINES_TAB))
            .build()
        val BATTERY: BlockEntityType<BlockEntityBattery> = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            "$MOD_ID:basic_battery",
            BlockEntityType.Builder.create({ BlockEntityBattery() }, BATTERY_BLOCK).build(null)
        )

        // Dynamo
        val DYNAMO_BLOCK: Block = RegisteredBlockBuilder(
            "dynamo",
            BlockDynamo(FabricBlockSettings.of(MATERIAL_METAL).hardness(3.0f))
        )
            .withSimpleSidedModel()
            .withFacingBlockState()
            .withGeneratedItem(FabricItemSettings().group(MACHINES_TAB))
            .build()
        val DYNAMO: BlockEntityType<BlockEntityDynamo> = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            "$MOD_ID:dynamo",
            BlockEntityType.Builder.create({ BlockEntityDynamo() }, DYNAMO_BLOCK).build(null)
        )

        // Crank
        val CRANK_HANDLE_BLOCK: Block = RegisteredBlockBuilder(
            "crank_handle",
            BlockCrankHandle(FabricBlockSettings.of(MATERIAL_WOOD).nonOpaque())
        )
            .withEmptyBlock()
            .withNoModelItem(FabricItemSettings().group(MACHINES_TAB))
            .build()
        val CRANK_HANDLE: BlockEntityType<BlockEntityCrankHandle> = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            "$MOD_ID:crank_handle",
            BlockEntityType.Builder.create({ BlockEntityCrankHandle() }, CRANK_HANDLE_BLOCK)
                .build(null)
        )

        // Debug stuff
        val DEBUG_LINKER: ItemWireSpool = registerItem("debug_linker", ItemWireSpool(Item.Settings().maxCount(1).group(DEBUG_TAB), Int.MAX_VALUE, 0.1f, intArrayOf(255, 0, 255), true))

        // Wiring
        val CONNECTOR_T0_BLOCK: Block = RegisteredBlockBuilder(
            "connector_t0",
            BlockWireConnectorT0(FabricBlockSettings.of(Material.STONE).hardness(1.0f))
        )
            .withGeneratedItem(FabricItemSettings().group(MACHINES_TAB))
            .build()
        val CONNECTOR_T0: BlockEntityType<BlockEntityWireConnectorT0> = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            "$MOD_ID:connector_t0",
            BlockEntityType.Builder.create({ BlockEntityWireConnectorT0() }, CONNECTOR_T0_BLOCK).build(null)
        )

        val MULTIBLOCK_CHILD_BLOCK: Block = RegisteredBlockBuilder(
            "multiblock_child",
            BlockMultiblockChild(FabricBlockSettings.of(MATERIAL_STONE).hardness(2.0f).nonOpaque())
        )
            .withEmptyBlock()
            .build()
        val MULTIBLOCK_CHILD: BlockEntityType<BlockEntityMultiblockChild> = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                "$MOD_ID:multiblock_child",
                BlockEntityType.Builder.create({ BlockEntityMultiblockChild() }, MULTIBLOCK_CHILD_BLOCK).build(null)
        )

        // Multiblocks
        val MULTIBLOCKS: HashSet<BlockMultiblockRoot> = HashSet()
        val COKE_OVEN_DUMMY_ITEM: Item = registerItem("coke_oven_dummy", Item(FabricItemSettings()))
        val COKE_OVEN_MULTIBLOCK_BLOCK: BlockMultiblockRoot = Registry.register(
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

        val BLAST_FURNACE_DUMMY_ITEM: Item = registerItem("blast_furnace_dummy", Item(FabricItemSettings()))
        val BLAST_FURNACE_MULTIBLOCK_BLOCK: BlockMultiblockRoot = Registry.register(
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
                Identifier(MOD_ID, "blast_furnace_multiblock")
        ) { syncId, inventory -> BlastFurnaceGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY) }

        // Dummy blocks for rendering etc.
        val CONNECTOR_DUMMY: Block = Registry.register(Registry.BLOCK, Identifier(MOD_ID, "connector_dummy"), BlockConnectorDummy(FabricBlockSettings.of(Material.AIR)))
        val CRANK_HANDLE_DUMMY: Block = Registry.register(Registry.BLOCK, Identifier(MOD_ID, "crank_handle_dummy"), Block(FabricBlockSettings.of(Material.AIR)))

        // Misc
        val TATER: Block = RegisteredBlockBuilder(
            "tater",
            BlockTater(FabricBlockSettings.of(MATERIAL_STONE).hardness(2.0f))
        )
            .withGeneratedItem(FabricItemSettings().group(TOOLS_TAB))
            .build()

        // Recipes
        val COKING_RECIPE_TYPE: RecipeType<CokingRecipe> = Registry.register(
            Registry.RECIPE_TYPE,
            Identifier(MOD_ID, "coking"),
            object: RecipeType<CokingRecipe> {}
        )
        val COKING_RECIPE_SERIALIZER: RecipeSerializer<*> = Registry.register(
            Registry.RECIPE_SERIALIZER,
            Identifier(MOD_ID, "coking"),
            ProcessingRecipeSerializer(object: ProcessingRecipeSerializer.Factory<CokingRecipe> {
                override fun createRecipe(
                    id: Identifier,
                    inputs: ArrayList<Ingredient>,
                    output: ItemStack,
                    processingTime: Int
                ): CokingRecipe {
                    return CokingRecipe(id, inputs, output, processingTime)
                }
            })
        )

        val BLASTING_RECIPE_TYPE: RecipeType<BlastingRecipe> = Registry.register(
            Registry.RECIPE_TYPE,
            Identifier(MOD_ID, "blasting"),
            object: RecipeType<BlastingRecipe> {}
        )
        val BLASTING_RECIPE_SERIALIZER: RecipeSerializer<*> = Registry.register(
            Registry.RECIPE_SERIALIZER,
            Identifier(MOD_ID, "blasting"),
            ProcessingRecipeSerializer(object: ProcessingRecipeSerializer.Factory<BlastingRecipe> {
                override fun createRecipe(
                    id: Identifier,
                    inputs: ArrayList<Ingredient>,
                    output: ItemStack,
                    processingTime: Int
                ): BlastingRecipe {
                    return BlastingRecipe(id, inputs, output, processingTime)
                }
            })
        )

        val SHAPELESS_TOOL_DAMAGING_RECIPE_TYPE: RecipeType<ShapelessToolDamagingRecipe> = Registry.register(
            Registry.RECIPE_TYPE,
            Identifier(MOD_ID, "crafting_shapeless_tooldamage"),
            object: RecipeType<ShapelessToolDamagingRecipe> {}
        )
        val SHAPELESS_TOOL_DAMAGING_RECIPE_SERIALIZER: RecipeSerializer<*> = Registry.register(
            Registry.RECIPE_SERIALIZER,
            Identifier(MOD_ID, "crafting_shapeless_tooldamage"),
            ShapelessToolDamagingRecipeSerializer()
        )
    }

    init {
        OBJLoader.INSTANCE.registerDomain(MOD_ID)

        MULTIBLOCKS.add(COKE_OVEN_MULTIBLOCK_BLOCK)
        MULTIBLOCKS.add(BLAST_FURNACE_MULTIBLOCK_BLOCK)

        ClientBookRegistry.INSTANCE.pageTypes[Identifier(MOD_ID, "coking")] = PatchouliPageCoking::class.java
        ClientBookRegistry.INSTANCE.pageTypes[Identifier(MOD_ID, "blasting")] = PatchouliPageBlasting::class.java

        FuelRegistry.INSTANCE.add(COKE as ItemConvertible, 3200)
        FuelRegistry.INSTANCE.add(COKE_BLOCK as ItemConvertible, 32000)
    }

    override fun onInitialize() {
        // Add recipes
        COPPER = COPPER
            //.addIngotRecipe()
            .addNuggetRecipe()
            .addStickRecipe()
            .addPlateRecipe()
            .addWireRecipe()
            .addBlockRecipe()
            .addAxeRecipe()
            .addPickaxeRecipe()
            .addShovelRecipe()
            .addSwordRecipe()
            .addHoeRecipe()
        LEAD = LEAD
            //.addIngotRecipe()
            .addNuggetRecipe()
            .addStickRecipe()
            .addPlateRecipe()
            .addBlockRecipe()
            .addAxeRecipe()
            .addPickaxeRecipe()
            .addShovelRecipe()
            .addSwordRecipe()
            .addHoeRecipe()
        STEEL = STEEL
            //.addIngotRecipe()
            .addNuggetRecipe()
            .addStickRecipe()
            .addPlateRecipe()
            .addBlockRecipe()
        IRON = IRON
            .addStickRecipe()
            .addPlateRecipe()
        GOLD = GOLD
            .addStickRecipe()
            .addPlateRecipe()

        DataGenerator.commit()
    }
}