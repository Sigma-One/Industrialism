package sigmaone.industrialism;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import sigmaone.industrialism.common.material.Metal;

import static sigmaone.industrialism.common.RegistryHelper.*;

public class Industrialism implements ModInitializer {

	public static final String MOD_ID = "industrialism";

    // Add creative tab
    public static final ItemGroup INDUSTRIALISM_MATERIALS = FabricItemGroupBuilder.build(
            new Identifier(Industrialism.MOD_ID, "general"), () -> new ItemStack(Blocks.COMMAND_BLOCK)
    );

    public static Metal COPPER;

    @Override
    public void onInitialize() {
        COPPER = new Metal("copper", 4.5f).addOre("malachite", 1, 14, 8, 40, 64);
    }
}
