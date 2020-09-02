package sigmaone.industrialism.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.*;
import sigmaone.industrialism.Industrialism;
import java.util.stream.Collectors;

public class RegistryHelper {

    public static <I extends Item> I registerItem(String id, I item) {
        Registry.register(Registry.ITEM, new Identifier(Industrialism.MOD_ID, id), item);
        return item;
    }

    public static Block registerBlock(String id, Block block, ItemGroup category) {
        Registry.register(Registry.BLOCK, new Identifier(Industrialism.MOD_ID, id), block);
        Registry.register(Registry.ITEM, new Identifier(Industrialism.MOD_ID, id), new BlockItem(block, new Item.Settings().group(category)));
        return block;
    }

    public static ConfiguredFeature<?, ?> registerFeature(String id, ConfiguredFeature<?, ?> configuredFeature) {
         return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, id, configuredFeature);
    }

    public static void registerOreGen(String id, int dimension, Block ore, int size, int veins, int bottomOffset, int topOffset, int maxY) {
        ConfiguredFeature<?, ?> oreFeature;
        switch (dimension) {
            case 0: oreFeature = registerFeature(id, (ConfiguredFeature)((ConfiguredFeature)Feature.ORE
                        .configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ore.getDefaultState(), size))
                        .decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(bottomOffset, topOffset, maxY)))
                        .spreadHorizontally()).repeat(veins)); break;
            default: throw new IllegalStateException("Illegal dimension: " + dimension);
        }

        // Ore generation registering
        // Adapted from https://gist.github.com/CorgiTaco/3eb2d9128a1ec41bd5d5846d17994851

        // Loop through registered biomes
        for (Biome biome : BuiltinRegistries.BIOME) {
            // Convert biome's generation settings to mutable if it's immutable
            if (biome.getGenerationSettings().features instanceof ImmutableList) {
                biome.getGenerationSettings().features = biome.getGenerationSettings().features.stream().map(Lists::newArrayList).collect(Collectors.toList());
            }

            // Make sure biome is in the overworld
            // TODO: Fix ores automatically generating in custom dimensions
            if (biome.getCategory() != Biome.Category.NETHER && biome.getCategory() != Biome.Category.THEEND) {
                // Add generation to biome features
                while (biome.getGenerationSettings().features.size() <= GenerationStep.Feature.UNDERGROUND_ORES.ordinal()) {
                    biome.getGenerationSettings().features.add(Lists.newArrayList());
                }
                biome.getGenerationSettings().features.get(GenerationStep.Feature.UNDERGROUND_ORES.ordinal()).add(() -> oreFeature);
            }
        }
    }
}
