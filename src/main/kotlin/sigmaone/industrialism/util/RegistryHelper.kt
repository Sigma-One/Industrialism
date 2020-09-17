package sigmaone.industrialism.util

import com.google.common.collect.ImmutableList
import com.google.common.collect.Lists
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.decorator.Decorator
import net.minecraft.world.gen.decorator.RangeDecoratorConfig
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.OreFeatureConfig
import sigmaone.industrialism.Industrialism
import java.util.function.Supplier
import java.util.stream.Collectors

object RegistryHelper {
    @JvmStatic
    fun <I : Item?> registerItem(id: String?, item: I): I {
        Registry.register(Registry.ITEM, Identifier(Industrialism.MOD_ID, id), item)
        return item
    }

    @JvmStatic
    fun registerBlock(id: String, block: Block, category: ItemGroup?): Block {
        Registry.register(Registry.BLOCK, Identifier(Industrialism.MOD_ID, id), block)
        Registry.register(Registry.ITEM, Identifier(Industrialism.MOD_ID, id), BlockItem(block, Item.Settings().group(category)))
        return block
    }

    fun registerFeature(id: String?, configuredFeature: ConfiguredFeature<*, *>?): ConfiguredFeature<*, *> {
        return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, id, configuredFeature)!!
    }

    fun registerOreGen(id: String?, dimension: Int, ore: Block, size: Int, veins: Int, bottomOffset: Int, topOffset: Int, maxY: Int) {
        val oreFeature: ConfiguredFeature<*, *> = when (dimension) {
            0 -> registerFeature(id, (Feature.ORE
                    .configure(OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ore.defaultState, size))
                    .decorate(Decorator.RANGE.configure(RangeDecoratorConfig(bottomOffset, topOffset, maxY)))
                    .spreadHorizontally() as ConfiguredFeature<*, *>).repeat(veins) as ConfiguredFeature<*, *>)
            else -> throw IllegalStateException("Illegal dimension: $dimension")
        }

        // Ore generation registering
        // Adapted from https://gist.github.com/CorgiTaco/3eb2d9128a1ec41bd5d5846d17994851

        // Loop through registered biomes
        for (biome in BuiltinRegistries.BIOME) {
            // Convert biome's generation settings to mutable if it's immutable
            if (biome.generationSettings.features is ImmutableList<*>) {
                biome.generationSettings.features = biome.generationSettings.features.toMutableList()
                for ((i, feature) in biome.generationSettings.features.withIndex()) {
                    if (feature is ImmutableList<*>) {
                        biome.generationSettings.features[i] = feature.toMutableList()
                    }
                }
            }

            // Make sure biome is in the overworld
            // TODO: Fix ores automatically generating in custom dimensions
            if (biome.category != Biome.Category.NETHER && biome.category != Biome.Category.THEEND) {
                // Add generation to biome features
                while (biome.generationSettings.features.size <= GenerationStep.Feature.UNDERGROUND_ORES.ordinal) {
                    biome.generationSettings.features.add(Lists.newArrayList())
                }
                biome.generationSettings.features[GenerationStep.Feature.UNDERGROUND_ORES.ordinal].add(Supplier { oreFeature })
            }
        }
    }
}