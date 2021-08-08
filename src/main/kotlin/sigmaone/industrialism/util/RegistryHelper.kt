package sigmaone.industrialism.util

import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.structure.rule.RuleTest
import net.minecraft.util.Identifier
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.YOffset
import net.minecraft.world.gen.decorator.RangeDecoratorConfig
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.OreFeatureConfig
import net.minecraft.world.gen.heightprovider.UniformHeightProvider
import sigmaone.industrialism.Industrialism

object RegistryHelper {
    fun <I: Item> registerItem(id: String, item: I): I {
        return Registry.register(Registry.ITEM, Identifier(Industrialism.MOD_ID, id), item)
    }

    fun <T: Block> registerBlock(
        id: String,
        block: T
    ): T {
        Registry.register(Registry.BLOCK, Identifier(Industrialism.MOD_ID, id), block)
        return block
    }


    fun registerOreFeature(
        id: String?,
        ore: Block,
        featureRule: RuleTest,
        size: Int,
        veins: Int,
        bottom: Int,
        top: Int,
    ): ConfiguredFeature<*, *> {
        var oreFeature: ConfiguredFeature<*, *> = Feature.ORE
            .configure(OreFeatureConfig( // Feature Config
                    featureRule,
                    ore.defaultState,
                    size
            ))
            .range(RangeDecoratorConfig( // Generation range
                UniformHeightProvider.create(YOffset.aboveBottom(bottom), YOffset.fixed(top))
            ))
            .spreadHorizontally() // Who knows
            .repeat(veins) // Vein count per chunk



        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Identifier(Industrialism.MOD_ID, id), oreFeature)!!
        BiomeModifications.addFeature(
            BiomeSelectors.foundInOverworld(),
            GenerationStep.Feature.UNDERGROUND_ORES,
            RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, Identifier(Industrialism.MOD_ID, id))
        )
        return oreFeature
    }
}