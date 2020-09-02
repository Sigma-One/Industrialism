package sigmaone.industrialism;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import sigmaone.industrialism.block.BlockConnectorDummy;
import sigmaone.industrialism.block.machine.BlockBattery;
import sigmaone.industrialism.block.machine.BlockEntityBattery;
import sigmaone.industrialism.block.machine.BlockEntityManualGenerator;
import sigmaone.industrialism.block.machine.BlockManualGenerator;
import sigmaone.industrialism.block.wiring.*;
import sigmaone.industrialism.item.ItemScrewdriver;
import sigmaone.industrialism.item.ItemWireSpool;
import sigmaone.industrialism.material.metal.Metal;


import static sigmaone.industrialism.util.RegistryHelper.*;

public class Industrialism implements ModInitializer {

	public static final String MOD_ID = "industrialism";

    // Materials creative tab
    public static FabricItemGroupBuilder materialsTabBuilder = FabricItemGroupBuilder.create(new Identifier(Industrialism.MOD_ID, "materials"));
    public static ItemGroup MATERIALS_TAB;

    // Tools creative tab
    public static FabricItemGroupBuilder toolsTabBuilder = FabricItemGroupBuilder.create(new Identifier(Industrialism.MOD_ID, "tools"));
    public static ItemGroup TOOLS_TAB;

    // Machines creative tab
    public static FabricItemGroupBuilder machinesTabBuilder = FabricItemGroupBuilder.create(new Identifier(Industrialism.MOD_ID, "machines"));
    public static ItemGroup MACHINES_TAB;

    // Materials to fix mining levels
    public static final Material MATERIAL_STONE = new FabricMaterialBuilder(MaterialColor.STONE).build();
    public static final Material MATERIAL_METAL = new FabricMaterialBuilder(MaterialColor.GRAY).build();

    // Metals
    public static Metal COPPER;
    public static Metal LEAD;

    // Single block machines
    public static Block BATTERY_BLOCK;
    public static BlockEntityType<BlockEntityBattery> BATTERY;

    public static Block MANUAL_GENERATOR_BLOCK;
    public static BlockEntityType<BlockEntityManualGenerator> MANUAL_GENERATOR;

    // Wiring
    public static Block CONNECTOR_T0_BLOCK;
    public static BlockEntityType<BlockEntityWireConnector> CONNECTOR_T0;
    //public static Block CONNECTOR_T1_BLOCK;
    //public static BlockEntityType<BlockEntityWireConnector> CONNECTOR_T1;

    // Utility items
    public static Item SCREWDRIVER;
    public static ItemWireSpool DEBUG_LINKER;

    // Dummy blocks for rendering etc.
    public static Block CONNECTOR_DUMMY = Registry.register(Registry.BLOCK, new Identifier(Industrialism.MOD_ID, "connector_dummy"), new BlockConnectorDummy(FabricBlockSettings.of(Material.AIR)));

    // Misc
    public enum InputConfig {
        NONE,
        INPUT,
        OUTPUT
    }

    @Override
    public void onInitialize() {
        // Finalize creative tabs
        MATERIALS_TAB = materialsTabBuilder.icon(() -> new ItemStack(COPPER.ingot)).build();
        TOOLS_TAB = toolsTabBuilder.icon(() -> new ItemStack(COPPER.pickaxe)).build();
        MACHINES_TAB = machinesTabBuilder.icon(() -> new ItemStack(BATTERY_BLOCK)).build();

        SCREWDRIVER = registerItem("screwdriver", new ItemScrewdriver(new Item.Settings().group(Industrialism.TOOLS_TAB)));

        DEBUG_LINKER = registerItem("debug_linker", new ItemWireSpool(new Item.Settings().group(Industrialism.TOOLS_TAB)));

        COPPER = new Metal("copper", 4.5f)
                .addOre("malachite", 1, 14, 8, 40, 64)
                .addToolMaterial(3, 192, 2, 5.0f, 16)
                .addAxe(6, 0.9f).addPickaxe(1, 1.2f).addShovel(1.5f, 1.0f).addSword(3, 1.6f).addHoe(-2, 3.0f);

        LEAD = new Metal("lead", 4.5f)
                .addOre("galena", 1, 8, 6, 10, 40)
                .addToolMaterial(2, 135, 1, 3.0f, 7)
                .addAxe(6, 0.8f).addPickaxe(1, 1.1f).addShovel(1.5f, 0.9f).addSword(3, 1.5f).addHoe(-2, 2.9f);

        BATTERY_BLOCK = registerBlock("basic_battery", new BlockBattery(FabricBlockSettings.of(Industrialism.MATERIAL_METAL).hardness(3.0f)), Industrialism.MACHINES_TAB);
        BATTERY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Industrialism.MOD_ID+":"+"basic_battery", BlockEntityType.Builder.create(BlockEntityBattery::new, BATTERY_BLOCK).build(null));

        MANUAL_GENERATOR_BLOCK = registerBlock("manual_generator", new BlockManualGenerator((FabricBlockSettings.of(Industrialism.MATERIAL_METAL).hardness(3.0f))), Industrialism.MACHINES_TAB);
        MANUAL_GENERATOR = Registry.register(Registry.BLOCK_ENTITY_TYPE, Industrialism.MOD_ID+":"+"manual_generator", BlockEntityType.Builder.create(BlockEntityManualGenerator::new, MANUAL_GENERATOR_BLOCK).build(null));

        CONNECTOR_T0_BLOCK = registerBlock("connector_t0", new BlockWireConnectorT0(FabricBlockSettings.of(Material.STONE).hardness(1.0f)), Industrialism.MACHINES_TAB);
        CONNECTOR_T0 = Registry.register(Registry.BLOCK_ENTITY_TYPE, Industrialism.MOD_ID+":"+"connector_t0", BlockEntityType.Builder.create(BlockEntityWireConnector::new, CONNECTOR_T0_BLOCK).build(null));

        //CONNECTOR_T1_BLOCK = registerBlock("connector_t1", new BlockWireConnectorT1(FabricBlockSettings.of(Material.STONE).hardness(1.0f)), Industrialism.MACHINES_TAB);
        //CONNECTOR_T1 = Registry.register(Registry.BLOCK_ENTITY_TYPE, Industrialism.MOD_ID+":connector_t1", BlockEntityType.Builder.create(BlockEntityWireConnector::new, CONNECTOR_T0_BLOCK).build(null));
    }
}
