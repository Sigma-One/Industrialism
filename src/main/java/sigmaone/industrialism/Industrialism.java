package sigmaone.industrialism;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import sigmaone.industrialism.common.material.metal.Metal;

public class Industrialism implements ModInitializer {

	public static final String MOD_ID = "industrialism";

    // Add materials creative tab
    public static FabricItemGroupBuilder materialsTabBuilder = FabricItemGroupBuilder.create(new Identifier(Industrialism.MOD_ID, "materials"));
    public static ItemGroup MATERIALS_TAB;

    // Add tools creative tab
    public static FabricItemGroupBuilder toolsTabBuilder = FabricItemGroupBuilder.create(new Identifier(Industrialism.MOD_ID, "tools"));
    public static ItemGroup TOOLS_TAB;


    // Add materials to fix mining levels
    public static final Material MATERIAL_STONE = new FabricMaterialBuilder(MaterialColor.STONE).build();
    public static final Material MATERIAL_METAL = new FabricMaterialBuilder(MaterialColor.GRAY).build();

    public static Metal COPPER;

    @Override
    public void onInitialize() {
        COPPER = new Metal("copper", 4.5f)
                .addOre("malachite", 1, 14, 8, 40, 64)
                .addToolMaterial(3, 192, 2, 5.0f, 16)
                .addAxe(6, 0.9f).addPickaxe(1, 1.2f).addShovel(1.5f, 1.0f).addSword(3, 1.6f).addHoe(-2, 3.0f);

        // Finalize creative tabs
        MATERIALS_TAB = materialsTabBuilder.icon(() -> new ItemStack(COPPER.ingot)).build();
        TOOLS_TAB = toolsTabBuilder.icon(() -> new ItemStack(COPPER.pickaxe)).build();
    }
}
