package sigmaone.industrialism.common;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import sigmaone.industrialism.Industrialism;

public class RegistryHelper {

    private static OreGenerator oregen = new OreGenerator();

    public static Item registerItem(String id, Item item) {
        Registry.register(Registry.ITEM, new Identifier(Industrialism.MOD_ID, id), item);
        return item;
    }

    public static Block registerBlock(String id, Block block, ItemGroup category) {
        Registry.register(Registry.BLOCK, new Identifier(Industrialism.MOD_ID, id), block);
        Registry.register(Registry.ITEM, new Identifier(Industrialism.MOD_ID, id), new BlockItem(block, new Item.Settings().group(category)));
        return block;
    }

    public static void registerOreGen(int dimension, Block ore, int size, int veins, int bottom_offset, int top_offset, int max_y) {
        // Add generation to existing biomes
        Registry.BIOME.forEach((Biome biome) -> oregen.generate(dimension, biome, ore, size, veins, bottom_offset, top_offset, max_y));
        // Register listener for new biomes
        RegistryEntryAddedCallback.event(Registry.BIOME).register((i, identifier, biome) -> oregen.generate(dimension, biome, ore, size, veins, bottom_offset, top_offset, max_y));
    }
}
